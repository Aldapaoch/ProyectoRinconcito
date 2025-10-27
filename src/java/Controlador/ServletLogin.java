
package Controlador;

import Modelo.usuario;
import dao.usuarioDao;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ServletLogin")
public class ServletLogin extends HttpServlet {

    private final usuarioDao usuarioDAO = new usuarioDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String correo = request.getParameter("correo");
        String contrasena = request.getParameter("contrasena");

        if (correo == null || contrasena == null || correo.isEmpty() || contrasena.isEmpty()) {
            out.print("{\"status\":\"error\", \"message\":\"Faltan datos\"}");
            return;
        }

        usuario u = usuarioDAO.validarLogin(correo, contrasena);

        if (u != null) {
            // Guardar sesión en servidor (opcional si luego usarás sesiones reales)
            HttpSession session = request.getSession();
            session.setAttribute("usuarioActivo", u);

            // Respuesta JSON al frontend
            out.print("{\"status\":\"ok\", " +
                      "\"idUsuario\":" + u.getIdUsuario() + "," +
                      "\"nombre\":\"" + u.getNombre() + "\"," +
                      "\"apellido\":\"" + u.getApellido() + "\"}");
        } else {
            out.print("{\"status\":\"error\", \"message\":\"Correo o contraseña incorrectos\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().print("⚠️ Este servlet solo acepta peticiones POST");
    }
}