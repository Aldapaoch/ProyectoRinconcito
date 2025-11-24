
package Controlador;
import Modelo.registro;
import Modelo.usuario;
import dao.registroDao;
import dao.usuarioDao;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ServletUsuario")
public class ServletUsuario extends HttpServlet {

    private final usuarioDao usuarioDAO = new usuarioDao();
    private final registroDao registroDAO = new registroDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String accion = request.getParameter("accion");
        System.out.println(" [ServletUsuario] doPost ejecutado correctamente");

        if (accion == null || accion.isEmpty()) {
            out.print("⚠️ No se recibió ninguna acción.");
            System.out.println("❌ ERROR: Parámetro 'accion' no recibido en la petición POST");
            return;
        }

        System.out.println(" Acción recibida: " + accion);

        switch (accion) {
            case "registrar":
                registrarUsuario(request, out);
                break;

            case "login":
                iniciarSesion(request, out);
                break;

            default:
                out.print("❌ Acción no válida: " + accion);
                System.out.println("️ Acción no reconocida: " + accion);
                break;
        }
    }

    // ============================================================
    // 🔹 REGISTRAR USUARIO
    // ============================================================
    private void registrarUsuario(HttpServletRequest request, PrintWriter out) {
        System.out.println("🧩 Entrando a registrarUsuario()");
        try {
            registro r = new registro();
            r.setNombre(request.getParameter("nombre"));
            r.setApellido(request.getParameter("apellido"));
            r.setDni(request.getParameter("dni"));
            r.setTelefono(request.getParameter("telefono"));
            r.setCorreo(request.getParameter("correo"));
            r.setContrasena(request.getParameter("contrasena"));

            // Mostrar lo recibido
            System.out.println("?Datos recibidos en el servlet:");
            System.out.println(" - Nombre: " + r.getNombre());
            System.out.println(" - Apellido: " + r.getApellido());
            System.out.println(" - DNI: " + r.getDni());
            System.out.println(" - Teléfono: " + r.getTelefono());
            System.out.println(" - Correo: " + r.getCorreo());
            System.out.println(" - Contraseña: " + r.getContrasena());

            // Guardar en la base de datos
            System.out.println("?Intentando registrar en la base de datos...");
            boolean ok = registroDAO.registrar(r);

            if (ok) {
                usuarioDAO.crearCuentaUsuario(r.getId(), r.getCorreo(), r.getContrasena());
                out.print("✅ Usuario registrado correctamente.");
                System.out.println("✅ Usuario registrado correctamente");
            } else {
                out.print("❌ No se pudo registrar: DNI o correo ya registrados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("❌ Error interno: " + e.getMessage());
            System.out.println("💥 Excepción en registrarUsuario: " + e.getMessage());
        }
    }

    // ============================================================
    //  LOGIN DE USUARIO
    // ============================================================
    private void iniciarSesion(HttpServletRequest request, PrintWriter out) {
        System.out.println("🔑 Entrando a iniciarSesion()");
        try {
            String correo = request.getParameter("correo");
            String contrasena = request.getParameter("contrasena");

            System.out.println("📩 Datos recibidos para login:");
            System.out.println(" - Correo: " + correo);
            System.out.println(" - Contraseña: " + contrasena);

            usuario u = usuarioDAO.validarLogin(correo, contrasena);

            if (u != null) {
                out.print("✅ Sesión iniciada correctamente.");
                System.out.println("✅ Login exitoso para usuario: " + correo);
            } else {
                out.print("❌ Credenciales incorrectas.");
                System.out.println("❌ Login fallido para: " + correo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("❌ Error interno al iniciar sesión: " + e.getMessage());
            System.out.println(" Excepción en iniciarSesion: " + e.getMessage());
        }
    }
}
