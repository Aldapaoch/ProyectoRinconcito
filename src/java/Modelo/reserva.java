
package Modelo;

import java.time.LocalDateTime;

    public class reserva {
    private int idReserva;      // id_reserva (PK)
    private int usuarioId;      // FK -> Usuario.id_usuario
    private String nombre;      // nombre del cliente
    private String apellido;    // apellido del cliente
    private String dni;         // dni del cliente
    private String correo;      // correo del cliente
    private String telefono;    // teléfono del cliente
    private int personas;       // cantidad de personas
    private String fecha;       // fecha de la reserva (formato yyyy-MM-dd)
    private String hora;        // hora de la reserva (formato HH:mm)
    private String vista;       // vista seleccionada (Vista al Mar / Vista a la Terraza)

    // ======== Getters y Setters ==========
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getPersonas() {
        return personas;
    }

    public void setPersonas(int personas) {
        this.personas = personas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getVista() {
        return vista;
    }

    public void setVista(String vista) {
        this.vista = vista;
    }
}