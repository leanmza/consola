package com.leandro.clinica;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class EnvTester {

    // Punto de entrada: ejecuta todas las comprobaciones y devuelve 0 si todo está OK, 1 si hay fallos
    public static void main(String[] args) {
        int failures = 0;
        failures += checkJava();
        Properties props = loadProps();
        failures += checkProps(props);
        failures += checkMysqlDriver();
        failures += checkDb(props);
        printResult(failures);
        System.exit(failures == 0 ? 0 : 1);
    }

    // Verifica versión de Java instalada y datos básicos del runtime
    private static int checkJava() {
        String version = System.getProperty("java.version", "?");
        String vendor = System.getProperty("java.vendor", "?");
        String home = System.getProperty("java.home", "?");
        String tz = ZoneId.systemDefault().toString();
        boolean ok = version.startsWith("17") || version.startsWith("18") || version.startsWith("19") || version.startsWith("20") || version.startsWith("21");
        section("JAVA");
        line("Version", version, ok);
        line("Vendor", vendor, true);
        line("Home", home, true);
        line("Timezone", tz, true);
        return ok ? 0 : 1;
    }

    // Carga el archivo application.properties desde el classpath
    private static Properties loadProps() {
        section("PROPERTIES");
        Properties p = new Properties();
        try (InputStream in = EnvTester.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                line("application.properties", "No encontrado en classpath", false);
                return p;
            }
            p.load(in);
            line("application.properties", "Cargado", true);
        } catch (Exception e) {
            line("application.properties", "Error: " + e.getMessage(), false);
        }
        return p;
    }

    // Verifica que existan y/o muestren las propiedades mínimas requeridas
    private static int checkProps(Properties p) {
        int fail = 0;
        fail += require(p, "spring.datasource.url");
        fail += require(p, "spring.datasource.username");
        if (!p.containsKey("spring.datasource.password")) {
            line("spring.datasource.password", "Falta (se usará vacío)", true);
        } else {
            line("spring.datasource.password", mask(p.getProperty("spring.datasource.password")), true);
        }
        line("spring.jpa.hibernate.ddl-auto", p.getProperty("spring.jpa.hibernate.ddl-auto", "(no especificado)"), true);
        line("spring.jpa.database-platform", p.getProperty("spring.jpa.database-platform", "(no especificado)"), true);
        line("server.port", p.getProperty("server.port", "(no especificado)"), true);
        return fail;
    }

    // Marca como FAIL si falta una propiedad requerida
    private static int require(Properties p, String key) {
        if (!p.containsKey(key) || p.getProperty(key).isBlank()) {
            line(key, "Falta", false);
            return 1;
        }
        line(key, p.getProperty(key), true);
        return 0;
    }

    // Comprueba que el driver de MySQL esté disponible en el classpath
    private static int checkMysqlDriver() {
        section("MYSQL DRIVER");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            line("com.mysql.cj.jdbc.Driver", "Disponible", true);
            return 0;
        } catch (Throwable t) {
            line("com.mysql.cj.jdbc.Driver", "No disponible: " + t.getClass().getSimpleName(), false);
            return 1;
        }
    }

    // Intenta conectar a la base con las credenciales y verifica tablas y conteos
    private static int checkDb(Properties p) {
        section("DATABASE");
        String url = p.getProperty("spring.datasource.url", "");
        String user = p.getProperty("spring.datasource.username", "");
        String pass = p.getProperty("spring.datasource.password", "");
        if (url.isBlank() || user.isBlank()) {
            line("Conexion", "Config incompleta", false);
            return 1;
        }
        try (Connection cn = DriverManager.getConnection(url, user, pass)) {
            line("Conexion", "OK", true);
            DatabaseMetaData md = cn.getMetaData();
            line("DBMS", md.getDatabaseProductName() + " " + md.getDatabaseProductVersion(), true);
            Map<String, String> tablas = new LinkedHashMap<>();
            tablas.put("paciente", "id,nombre,apellido,email,celular");
            tablas.put("doctor", "id,nombre,apellido,email,celular");
            tablas.put("especialidad", "id,nombre");
            tablas.put("horarios", "id,hora_inicio,hora_fin");
            tablas.put("turno", "id,fecha_hora,ocupado,paciente_id,doctor_id");
            int fail = 0;
            for (String t : tablas.keySet()) {
                boolean exists = tableExists(md, t);
                line("Tabla " + t, exists ? "OK" : "No existe", exists);
                if (!exists) fail++;
            }
            fail += count(cn, "paciente");
            fail += count(cn, "doctor");
            fail += count(cn, "turno");
            return fail;
        } catch (Exception e) {
            line("Conexion", "ERROR: " + e.getMessage(), false);
            return 1;
        }
    }

    // Verifica si una tabla existe (probando en minúsculas y MAYÚSCULAS)
    private static boolean tableExists(DatabaseMetaData md, String table) {
        try (ResultSet rs = md.getTables(null, null, table, null)) {
            if (rs.next()) return true;
            try (ResultSet rs2 = md.getTables(null, null, table.toUpperCase(), null)) {
                return rs2.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    // Ejecuta SELECT COUNT(*) sobre la tabla dada y muestra el resultado
    private static int count(Connection cn, String table) {
        try (PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM " + table);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                line("Filas " + table, String.valueOf(rs.getLong(1)), true);
                return 0;
            }
        } catch (Exception e) {
            line("Filas " + table, "ERROR: " + e.getMessage(), false);
            return 1;
        }
        line("Filas " + table, "ERROR", false);
        return 1;
    }

    // Dibuja un encabezado ASCII para separar secciones del reporte
    private static void section(String title) {
        System.out.println("+--------------------------------------------------+");
        System.out.println("| " + pad(center(title, 46), 46) + " |");
        System.out.println("+--------------------------------------------------+");
    }

    // Imprime una línea de resultado con estado [OK]/[FAIL]
    private static void line(String key, String value, boolean ok) {
        String status = ok ? "OK" : "FAIL";
        String msg = String.format("%-24s : %-40s [%s]", key, trim(value, 40), status);
        System.out.println(msg);
    }

    // Centra texto en un ancho fijo
    private static String center(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;
        return " ".repeat(left) + s + " ".repeat(right);
        
    }

    // Rellena con espacios a la derecha hasta el ancho dado
    private static String pad(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }

    // Recorta cadenas largas para no romper el formato
    private static String trim(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    // Enmascara contraseñas evitando mostrarlas en claro
    private static String mask(String s) {
        if (s == null || s.isBlank()) return "(vacío)";
        return "*".repeat(Math.min(8, s.length()));
    }

    // Resumen final del estado del ambiente
    private static void printResult(int failures) {
        section("RESULTADO");
        if (failures == 0) {
            System.out.println("Ambiente OK. Todo funcionando.");
        } else {
            System.out.println("Se detectaron " + failures + " problema(s). Revise los items marcados como FAIL.");
        }
    }
}
