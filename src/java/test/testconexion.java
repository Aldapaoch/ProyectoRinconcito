
package test;

import dao.conexion;
import java.sql.Connection;
public class testconexion {
   public static void main(String[] args) {
        Connection conn = conexion.conectar();
        if (conn != null) {
            System.out.println("🎉 ¡Base de datos conectada correctamente!");
        } else {
            System.out.println("⚠️ No se pudo conectar a la base de datos.");
        }
    }
}
