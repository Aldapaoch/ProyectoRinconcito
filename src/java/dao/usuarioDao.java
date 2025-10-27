
package dao;

import Modelo.usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class usuarioDao {

    // 🟢 Crear cuenta de usuario
    public boolean crearCuentaUsuario(int registroId, String correo, String contrasena) {
        String sql = "INSERT INTO Usuario (registro_id, correo, contrasena) VALUES (?, ?, ?)";
        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, registroId);
            ps.setString(2, correo);
            ps.setString(3, contrasena);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al crear cuenta usuario: " + e.getMessage());
        }
        return false;
    }

    // 🟢 Validar login y traer datos completos del usuario
    public usuario validarLogin(String correo, String contrasena) {
        usuario u = null;

        String sql = """
            SELECT 
                u.id_usuario, u.registro_id, u.correo, u.contrasena,
                r.nombre, r.apellido, r.dni, r.telefono, r.correo_electronico
            FROM Usuario u
            INNER JOIN Registro r ON u.registro_id = r.id_registro
            WHERE u.correo = ? AND u.contrasena = ?
        """;

        try (Connection cn = conexion.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                u = new usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setRegistroId(rs.getInt("registro_id"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setDni(rs.getString("dni"));
                u.setTelefono(rs.getString("telefono"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al validar login: " + e.getMessage());
        }

        return u;
    }
}