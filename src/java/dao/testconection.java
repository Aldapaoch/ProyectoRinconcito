package dao;

import java.sql.Connection;
import java.sql.*;
public class testconection {
    public static void main(String[] args) {
       
        Connection conn = conexion.conectar();
        if (conn != null) {
            System.out.println("🎉 ¡Base de datos conectada correctamente!");
            try {
                conn.close(); // cerramos la conexión al final
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ No se pudo conectar a la base de datos.");
        }
    }
}
