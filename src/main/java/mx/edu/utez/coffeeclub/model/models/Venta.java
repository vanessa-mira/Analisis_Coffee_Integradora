package mx.edu.utez.coffeeclub.model.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venta {

    public static final String EFECTIVO = "EFECTIVO";
    public static final String TARJETA = "TARJETA";

    private int idVenta;
    private LocalDateTime fecha;
    private double total;
    private String metodoPago;
    private Double montoRecibido;   // solo en efectivo
    private Double cambio;          // solo en efectivo
    private int idUsuario;
    private String nombreEmpleado;
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public Double getMontoRecibido() { return montoRecibido; }
    public void setMontoRecibido(Double montoRecibido) { this.montoRecibido = montoRecibido; }

    public Double getCambio() { return cambio; }
    public void setCambio(Double cambio) { this.cambio = cambio; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }

    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getMetodoPagoTexto() {
        return EFECTIVO.equals(metodoPago) ? "Efectivo" : "Tarjeta";
    }
}
