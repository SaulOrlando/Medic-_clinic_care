package org.esfe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@SpringBootApplication
public class MediclinicAppApplication {

    public static void main(String[] args) {
        initDatabase();
        SpringApplication.run(MediclinicAppApplication.class, args);
    }

    private static void initDatabase() {
        String user = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "mediclinic_app";
        String pass = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "Med1cl1nic!2024";
        String dbName = "SistemaClinico";

        String masterUrl = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true";
        String dbUrl = "jdbc:sqlserver://localhost:1433;databaseName=" + dbName + ";encrypt=true;trustServerCertificate=true";

        try (Connection conn = DriverManager.getConnection(masterUrl, user, pass)) {
            Statement stmt = conn.createStatement();

            var rs = stmt.executeQuery("SELECT name FROM sys.databases WHERE name = '" + dbName + "'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE [" + dbName + "]");
                System.out.println("[DB] Base de datos '" + dbName + "' creada.");
            } else {
                System.out.println("[DB] Base de datos '" + dbName + "' ya existe.");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("[DB] Error al verificar/crear BD: " + e.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, user, pass)) {
            conn.close();
            System.out.println("[DB] Conexion a '" + dbName + "' verificada.");
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Cannot open database")) {
                System.out.println("[DB] El login '" + user + "' no tiene acceso a '" + dbName + "'.");
                System.out.println("[DB] Ejecuta este SQL en SQL Server Management Studio:");
                System.out.println();
                System.out.println("  USE [master];");
                System.out.println("  ALTER LOGIN [" + user + "] WITH DEFAULT_DATABASE = [" + dbName + "];");
                System.out.println("  USE [" + dbName + "];");
                System.out.println("  CREATE USER [" + user + "] FOR LOGIN [" + user + "];");
                System.out.println("  ALTER ROLE db_owner ADD MEMBER [" + user + "];");
                System.out.println();
            } else {
                System.out.println("[DB] Error de conexion: " + msg);
            }
        }
    }
}
