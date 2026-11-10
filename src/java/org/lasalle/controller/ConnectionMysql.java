/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a MySQL/MariaDB configurada por variables de entorno.
 *
 * Variables soportadas:
 *   DB_HOST      (default 127.0.0.1)
 *   DB_PORT      (default 3306)
 *   DB_NAME      (default gluco_api)
 *   DB_USER      (default root)
 *   DB_PASS      (default root)
 *   DB_SSL_MODE  (vacío para local; REQUIRED para MySQL externo con TLS)
 *
 * @author Halo
 */
public class ConnectionMysql {
    private Connection conn;

    public Connection open() throws SQLException {
        String host = getEnv("DB_HOST", "127.0.0.1");
        String port = getEnv("DB_PORT", "3306");
        String dbName = getEnv("DB_NAME", "gluco_api");
        String user = getEnv("DB_USER", "root");
        String password = getEnv("DB_PASS", "root");
        String sslMode = getEnv("DB_SSL_MODE", "");

        StringBuilder parametros = new StringBuilder(
                "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC");

        if (sslMode.isBlank()) {
            parametros.append("&useSSL=false&allowPublicKeyRetrieval=true");
        } else {
            parametros.append("&sslMode=").append(sslMode);
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + parametros;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL", e);
        }

        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }

    private String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
