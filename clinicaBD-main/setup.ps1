[CmdletBinding()]
Param(
  [string]$DbUser = "root",
  [securestring]$DbPassword,
  [string]$DbName = "clinica",
  [string]$DbHost = "localhost",
  [int]$DbPort = 3306,
  [switch]$SkipDbImport,
  [switch]$Run
)

function Write-Header($title) {
  Write-Host "`n==================================================" -ForegroundColor Cyan
  Write-Host " $title" -ForegroundColor Cyan
  Write-Host "==================================================" -ForegroundColor Cyan
}

function Assert-Tool($name, $cmd) {
  if (-not (Get-Command $cmd -ErrorAction SilentlyContinue)) {
    throw "No se encontró $name en PATH. Instálalo y vuelve a ejecutar."
  }
}

try {
  Write-Header "Chequeos previos"
  Assert-Tool "Java (JDK 17+)" "java"
  $javaVersion = (& java -version 2>&1 | Select-Object -First 1)
  Write-Host "Java: $javaVersion"

  $mvnw = Join-Path (Get-Location) "mvnw.cmd"
  if (-not (Test-Path $mvnw)) { throw "No se encontró mvnw.cmd en el proyecto." }
  Write-Host "Maven Wrapper OK"

  if (-not $SkipDbImport) {
    Write-Header "Base de datos MySQL"
    $mysqlCmd = Get-Command mysql -ErrorAction SilentlyContinue
    if (-not $mysqlCmd) {
      Write-Warning "No se encontró el cliente mysql en PATH. Se omitirá la importación del esquema."
    } else {
      if (-not $DbPassword) { $DbPassword = Read-Host -AsSecureString "Password de MySQL para usuario '$DbUser' (puede estar vacío)" }
      $pwdPlain = if ($DbPassword) { [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($DbPassword)) } else { "" }

      $sqlFile = Join-Path (Get-Location) "clinica_BD.sql"
      if (Test-Path $sqlFile) {
        Write-Host "Creando base de datos si no existe: $DbName" -ForegroundColor Yellow
        $createCmd = if ($pwdPlain -ne "") { "mysql -h $DbHost -P $DbPort -u $DbUser -p$pwdPlain -e \"CREATE DATABASE IF NOT EXISTS `$DbName`$DbName;\"" } else { "mysql -h $DbHost -P $DbPort -u $DbUser -e \"CREATE DATABASE IF NOT EXISTS `$DbName`$DbName;\"" }
        # Corrección del comando CREATE DATABASE
        $createCmd = if ($pwdPlain -ne "") { "mysql -h $DbHost -P $DbPort -u $DbUser -p$pwdPlain -e \"CREATE DATABASE IF NOT EXISTS `$DbName`;\"" } else { "mysql -h $DbHost -P $DbPort -u $DbUser -e \"CREATE DATABASE IF NOT EXISTS `$DbName`;\"" }
        cmd /c $createCmd | Out-Null

        Write-Host "Importando esquema desde clinica_BD.sql" -ForegroundColor Yellow
        $importCmd = if ($pwdPlain -ne "") { "mysql -h $DbHost -P $DbPort -u $DbUser -p$pwdPlain $DbName < `"$sqlFile`"" } else { "mysql -h $DbHost -P $DbPort -u $DbUser $DbName < `"$sqlFile`"" }
        cmd /c $importCmd
      } else {
        Write-Warning "No se encontró clinica_BD.sql; se omite importación."
      }
    }
  }

  Write-Header "Resolviendo dependencias y construyendo"
  & $mvnw -q dependency:resolve
  if ($LASTEXITCODE -ne 0) { throw "Fallo dependency:resolve" }
  & $mvnw -DskipTests package
  if ($LASTEXITCODE -ne 0) { throw "Fallo build Maven" }
  Write-Host "Build OK" -ForegroundColor Green

  if ($Run) {
    Write-Header "Iniciando aplicación"
    & $mvnw spring-boot:run
  } else {
    Write-Host "Para ejecutar ahora: .\mvnw.cmd spring-boot:run" -ForegroundColor Cyan
  }
}
catch {
  Write-Error $_
  exit 1
}
