package org.lasalle.controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionMysql {
    Connection conn;
    
    public Connection open(){
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASS");
        String db_name = System.getenv("DB_NAME");
        
        // Si no existen variables de entorno (entorno local), usa valores por defecto
        if (host == null) host = "TU_HOST_AIVEN";
        if (port == null) port = "TU_PUERTO_AIVEN";
        if (user == null) user = "TU_USER_AIVEN";
        if (password == null) password = "TU_PASSWORD_AIVEN";
        if (db_name == null) db_name = "defaultdb";

        String url = "jdbc:mysql://" + host + ":" + port + "/" + db_name;
        String parametros = "?useSSL=true&requireSSL=true&verifyServerCertificate=false&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=utf-8";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url + parametros, user, password);
            return conn;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        if(conn != null){
            try {
                conn.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}