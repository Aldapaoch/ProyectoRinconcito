
package dao;
import Modelo.reserva;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class reservaDao {

        public int registrarReserva(reserva r) {
        String sql = "INSERT INTO Reserva (usuario_id, nombre, apellido, dni, telefono, correo, personas, fecha, hora, vista) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getUsuarioId());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getApellido());
            ps.setString(4, r.getDni());
            ps.setString(5, r.getTelefono());
            ps.setString(6, r.getCorreo());
            ps.setInt(7, r.getPersonas());
            ps.setString(8, r.getFecha());
            ps.setString(9, r.getHora());
            ps.setString(10, r.getVista());

            System.out.println("Ejecutando SQL: " + sql);
            System.out.println("Datos enviados:");
            System.out.println("  usuario_id = " + r.getUsuarioId());
            System.out.println("  nombre = " + r.getNombre());
            System.out.println("  apellido = " + r.getApellido());
            System.out.println("  dni = " + r.getDni());
            System.out.println("  telefono = " + r.getTelefono());
            System.out.println("  correo = " + r.getCorreo());
            System.out.println("  personas = " + r.getPersonas());
            System.out.println("  fecha = " + r.getFecha());
            System.out.println("  hora = " + r.getHora());
            System.out.println("  vista = " + r.getVista());

            int filas = ps.executeUpdate();
            System.out.println(" Filas insertadas: " + filas);

            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idReserva = rs.getInt(1);
                    System.out.println("ID de reserva generado = " + idReserva);
                    return idReserva;
                }
            }

            return -1; 

        } catch (SQLException e) {
            System.out.println("Error SQL al registrar reserva: " + e.getMessage());
            return -1;
        }
    }
}
