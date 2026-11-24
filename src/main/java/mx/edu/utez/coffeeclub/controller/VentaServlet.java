package mx.edu.utez.coffeeclub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.coffeeclub.model.dao.ProductoDao;
import mx.edu.utez.coffeeclub.model.dao.VentaDao;
import mx.edu.utez.coffeeclub.model.models.DetalleVenta;
import mx.edu.utez.coffeeclub.model.models.Producto;
import mx.edu.utez.coffeeclub.model.models.Usuario;
import mx.edu.utez.coffeeclub.model.models.Venta;
import mx.edu.utez.coffeeclub.utils.Flash;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Punto de venta: menú digital, armado del pedido y registro del pago. */
@WebServlet(name = "VentaServlet", value = "/venta")
public class VentaServlet extends HttpServlet {

    private final ProductoDao productoDao = new ProductoDao();
    private final VentaDao ventaDao = new VentaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Flash.pasar(request, "error", "modalTitulo", "modalLineas");
        request.setAttribute("tituloPagina", "Punto de venta");
        request.setAttribute("productos", productoDao.getActivos());
        request.getRequestDispatcher("/WEB-INF/views/venta.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        String destino = request.getContextPath() + "/venta";

        // 1. Productos del pedido: "id:cantidad,id:cantidad"
        List<DetalleVenta> detalles = new ArrayList<>();
        double total = 0;
        String items = request.getParameter("items");
        if (items == null || items.isBlank()) {
            Flash.guardar(request, "error", "El pedido está vacío. Selecciona al menos un producto.");
            response.sendRedirect(destino);
            return;
        }
        try {
            for (String item : items.split(",")) {
                String[] partes = item.split(":");
                int idProducto = Integer.parseInt(partes[0].trim());
                int cantidad = Integer.parseInt(partes[1].trim());

                Producto p = productoDao.getById(idProducto);
                if (p == null || !p.isActivo() || cantidad < 1 || cantidad > 99) {
                    Flash.guardar(request, "error", "El pedido tiene un producto no disponible. Revisa el pedido.");
                    response.sendRedirect(destino);
                    return;
                }
                // El precio siempre se toma de la base de datos
                DetalleVenta d = new DetalleVenta();
                d.setIdProducto(p.getIdProducto());
                d.setNombreProducto(p.getNombre());
                d.setCantidad(cantidad);
                d.setPrecioUnitario(p.getPrecio());
                d.setSubtotal(redondear(p.getPrecio() * cantidad));
                detalles.add(d);
                total += d.getSubtotal();
            }
        } catch (RuntimeException e) {
            Flash.guardar(request, "error", "El pedido no es válido.");
            response.sendRedirect(destino);
            return;
        }
        total = redondear(total);

        // 2. Método de pago
        String metodo = request.getParameter("metodoPago");
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(total);
        venta.setIdUsuario(usuario.getIdUsuario());
        venta.setDetalles(detalles);

        if (Venta.EFECTIVO.equals(metodo)) {
            double recibido;
            try {
                recibido = Double.parseDouble(request.getParameter("montoRecibido"));
            } catch (RuntimeException e) {
                Flash.guardar(request, "error", "Escribe el monto recibido del cliente.");
                response.sendRedirect(destino);
                return;
            }
            if (recibido < total) {
                Flash.guardar(request, "error", "El monto recibido es insuficiente.");
                response.sendRedirect(destino);
                return;
            }
            venta.setMetodoPago(Venta.EFECTIVO);
            venta.setMontoRecibido(redondear(recibido));
            venta.setCambio(redondear(recibido - total));
        } else if (Venta.TARJETA.equals(metodo)) {
            // En tarjeta solo se guarda el método; no se guardan datos de la tarjeta
            venta.setMetodoPago(Venta.TARJETA);
        } else {
            Flash.guardar(request, "error", "Selecciona un método de pago.");
            response.sendRedirect(destino);
            return;
        }

        // 3. Guardar
        if (!ventaDao.create(venta)) {
            Flash.guardar(request, "error", "No se pudo registrar la venta. Inténtalo de nuevo.");
            response.sendRedirect(destino);
            return;
        }

        List<String> lineas = new ArrayList<>();
        lineas.add("Total cobrado: " + dinero(venta.getTotal()));
        lineas.add("Método de pago: " + venta.getMetodoPagoTexto());
        if (venta.getCambio() != null) {
            lineas.add("Cambio entregado: " + dinero(venta.getCambio()));
        }
        Flash.guardar(request, "modalTitulo", "Venta registrada");
        Flash.guardar(request, "modalLineas", lineas);
        response.sendRedirect(destino);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private String dinero(double valor) {
        return String.format(Locale.US, "$%,.2f", valor);
    }
}
