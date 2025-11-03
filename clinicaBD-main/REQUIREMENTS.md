# Requisitos y configuración automática

Este proyecto es una aplicación Spring Boot (Java) con base de datos MySQL. Maven (vía wrapper) gestiona las dependencias.

## Requisitos del sistema
- Java JDK 17+
- MySQL Server 8.x (local o remoto)
- Cliente de MySQL (mysql.exe) para importar el esquema (opcional pero recomendado)
- Windows PowerShell 5+ o PowerShell 7+ (para el script de instalación)

## Configuración de base de datos
- Archivo de propiedades: `src/main/resources/application.properties`
  - URL por defecto: `jdbc:mysql://localhost:3306/clinica?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires`
  - Usuario: `root`
  - Password: vacío
  - Dialecto: `org.hibernate.dialect.MySQLDialect`

Si tu entorno es distinto, ajusta esas variables o usa el script `setup.ps1` que te pedirá los datos.

## Esquema de BD
- Script SQL: `clinica_BD.sql`
- Puedes importarlo con el cliente de MySQL:
  - PowerShell/CMD:
    - Crear DB: `mysql -u <user> -p -e "CREATE DATABASE IF NOT EXISTS clinica;"`
    - Importar: `mysql -u <user> -p clinica < clinica_BD.sql`

## Dependencias del proyecto
Maven maneja todo en `pom.xml`. No hace falta instalar dependencias manualmente de Java.

## Ejecución rápida
- Con Maven Wrapper (recomendado):
  - Windows: `mvnw.cmd spring-boot:run`
- O construir y ejecutar:
  - `mvnw.cmd -DskipTests package`
  - `java -jar target/clinica-*.jar`

## Automatización (Windows)
- Ejecuta `./setup.ps1` desde PowerShell para:
  - Verificar Java 17
  - (Opcional) Crear DB y cargar `clinica_BD.sql`
  - Compilar y lanzar la app con Maven Wrapper

Si no tienes el cliente de MySQL en PATH, el paso de importación se omitirá o te pedirá la ruta.
