 
package Controlador;
import dao.reservaDao;
import Modelo.reserva;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ServletReserva")
@MultipartConfig
public class ServletReserva extends HttpServlet {

    private final reservaDao reservaDAO = new reservaDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String idParam = request.getParameter("usuario_id");
            if (idParam == null || idParam.isEmpty()) {
                idParam = request.getParameter("usuarioId");
            }
            if (idParam == null || idParam.isEmpty()) {
                idParam = request.getParameter("id");
            }

            System.out.println("? Parametro usuario_id recibido: " + idParam);

            if (idParam == null || idParam.isEmpty()) {
                out.print("️ Error: no se recibió el ID de usuario.");
                return;
            }

            int usuarioId = Integer.parseInt(idParam);

            String nombre = request.getParameter("nombre");
            String apellido = request.getParameter("apellido");
            String dni = request.getParameter("dni");
            String telefono = request.getParameter("telefono");
            String correo = request.getParameter("correo");
            String personasStr = request.getParameter("personas");
            String fecha = request.getParameter("fecha");
            String hora = request.getParameter("hora");
            String vista = request.getParameter("vista");

            if (nombre == null || apellido == null || dni == null || telefono == null ||
                correo == null || personasStr == null || fecha == null || hora == null || vista == null) {
                out.print("⚠️ Faltan datos obligatorios.");
                return;
            }

            int personas = Integer.parseInt(personasStr);

            reserva r = new reserva();
            r.setUsuarioId(usuarioId);
            r.setNombre(nombre);
            r.setApellido(apellido);
            r.setDni(dni);
            r.setTelefono(telefono);
            r.setCorreo(correo);
            r.setPersonas(personas);
            r.setFecha(fecha);
            r.setHora(hora);
            r.setVista(vista);

            System.out.println("🟢 Datos recibidos para reserva:");
            System.out.println("usuarioId=" + usuarioId);
            System.out.println("nombre=" + nombre);
            System.out.println("apellido=" + apellido);
            System.out.println("dni=" + dni);
            System.out.println("telefono=" + telefono);
            System.out.println("correo=" + correo);
            System.out.println("personas=" + personas);
            System.out.println("fecha=" + fecha);
            System.out.println("hora=" + hora);
            System.out.println("vista=" + vista);

            boolean ok = reservaDAO.registrarReserva(r);

            if (ok) {
                out.print("✅ Reserva registrada correctamente");
            } else {
                out.print("❌ Error al guardar en la base de datos");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("⚠️ Error interno: " + e.getMessage());
        }
    }
}