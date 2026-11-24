package mx.edu.utez.coffeeclub.model.models;

public class Usuario {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String EMPLEADO = "EMPLEADO";

    private int idUsuario;
    private String nombre;
    private String correo;
    private String telefono;
    private String password;   // contraseña cifrada
    private String rol;
    private boolean activo;
    private int totalVentas;   // solo para la lista de usuarios

    public Usuario() {
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int getTotalVentas() { return totalVentas; }
    public void setTotalVentas(int totalVentas) { this.totalVentas = totalVentas; }

    public boolean isAdministrador() { return ADMINISTRADOR.equals(rol); }

    public String getRolTexto() { return isAdministrador() ? "Administrador" : "Empleado"; }

    public String getPrimerNombre() {
        if (nombre == null || nombre.isBlank()) return "";
        return nombre.trim().split("\\s+")[0];
    }

    public String getIniciales() {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] p = nombre.trim().split("\\s+");
        String r = p[0].substring(0, 1);
        if (p.length > 1) r += p[1].substring(0, 1);
        return r.toUpperCase();
    }
}
