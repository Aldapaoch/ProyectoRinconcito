
package dao;

import Modelo.registro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class registroDao {
    public boolean registrar(registro r) {
        String sql = "INSERT INTO Registro (nombre, apellido, dni, telefono, correo_electronico, contrasena) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getNombre());
            ps.setString(2, r.getApellido());
            ps.setString(3, r.getDni());
            ps.setString(4, r.getTelefono());
            ps.setString(5, r.getCorreo());
            ps.setString(6, r.getContrasena());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    r.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error en registrarDao: " + e.getMessage());
        }
        return false;
    }
    public boolean existeDniOCorreo(String dni, String correo) {
    String sql = "SELECT COUNT(*) FROM Registro WHERE dni = ? OR correo_electronico = ?";

    try (Connection cn = conexion.conectar();
         PreparedStatement ps = cn.prepareStatement(sql)) {

        ps.setString(1, dni);
        ps.setString(2, correo);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        System.out.println("Error en existeDniOCorreo: " + e.getMessage());
    }
    return true;
}
}