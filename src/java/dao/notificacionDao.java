
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class notificacionDao {
     public boolean crearNotificacion(int reservaId) {

        String sql = "INSERT INTO Notificacion (reserva_id) VALUES (?)";

        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, reservaId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(" Error al crear notificación: " + e.getMessage());
            return false;
        }
    }
}

