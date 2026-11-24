package mx.edu.utez.coffeeclub.model.models;

public class Producto {

    public static final String BEBIDA = "BEBIDA";
    public static final String ALIMENTO = "ALIMENTO";

    private int idProducto;
    private String nombre;
    private String tipo;
    private double precio;
    private String descripcion;
    private boolean activo;

    public Producto() {
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getTipoTexto() { return BEBIDA.equals(tipo) ? "Bebida" : "Alimento"; }
}
