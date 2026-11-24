package mx.edu.utez.coffeeclub.model.models;

/** Una fila de "Ventas por empleado" en el historial de ventas. */
public class ResumenEmpleado {

    private String nombre;
    private boolean activo;
    private int ventas;
    private double total;

    public ResumenEmpleado() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public int getVentas() { return ventas; }
    public void setVentas(int ventas) { this.ventas = ventas; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
