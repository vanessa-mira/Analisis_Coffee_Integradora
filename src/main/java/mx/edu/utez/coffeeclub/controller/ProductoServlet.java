package mx.edu.utez.coffeeclub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.coffeeclub.model.dao.ProductoDao;
import mx.edu.utez.coffeeclub.model.models.Producto;
import mx.edu.utez.coffeeclub.utils.Flash;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Catálogo de productos (solo administrador): alta, modificación y habilitar / inhabilitar. */
@WebServlet(name = "ProductoServlet", value = "/productos")
public class ProductoServlet extends HttpServlet {

    private final ProductoDao dao = new ProductoDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("action");

        if ("nuevo".equals(accion)) {
            request.setAttribute("tituloPagina", "Agregar producto");
            request.setAttribute("valores", new Producto());
            request.getRequestDispatcher("/WEB-INF/views/productoForm.jsp").forward(request, response);
            return;
        }

        if ("editar".equals(accion)) {
            Producto p = null;
            try {
                p = dao.getById(Integer.parseInt(request.getParameter("id")));
            } catch (NumberFormatException e) {
                // se queda en null y regresa a la lista
            }
            if (p == null) {
                response.sendRedirect(request.getContextPath() + "/productos");
                return;
            }
            request.setAttribute("tituloPagina", "Editar producto");
            request.setAttribute("valores", p);
            request.setAttribute("editando", true);
            request.getRequestDispatcher("/WEB-INF/views/productoForm.jsp").forward(request, response);
            return;
        }

        String q = request.getParameter("q") == null ? "" : request.getParameter("q").trim();
        Flash.pasar(request, "error", "modalTitulo", "modalLineas");
        request.setAttribute("tituloPagina", "Catálogo de productos");
        request.setAttribute("disponibles", dao.buscar(q, true));
        request.setAttribute("inhabilitados", dao.buscar(q, false));
        request.setAttribute("q", q);
        request.getRequestDispatcher("/WEB-INF/views/productos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("action");
        if ("crear".equals(accion) || "actualizar".equals(accion)) {
            guardar(request, response, "actualizar".equals(accion));
        } else if ("estado".equals(accion)) {
            cambiarEstado(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/productos");
        }
    }

    private void guardar(HttpServletRequest request, HttpServletResponse response, boolean editando)
            throws ServletException, IOException {

        Producto p = new Producto();
        int id = 0;
        if (editando) {
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/productos");
                return;
            }
            p.setIdProducto(id);
        }
        p.setNombre(request.getParameter("nombre") == null ? "" : request.getParameter("nombre").trim());
        p.setTipo(request.getParameter("tipo"));
        p.setDescripcion(request.getParameter("descripcion") == null ? "" : request.getParameter("descripcion").trim());
        p.setActivo(true);

        List<String> errores = new ArrayList<>();
        if (p.getNombre().isEmpty()) {
            errores.add("El nombre del producto es obligatorio.");
        } else if (dao.existeNombre(p.getNombre(), id)) {
            errores.add("Ya existe un producto con ese nombre.");
        }
        if (!Producto.BEBIDA.equals(p.getTipo()) && !Producto.ALIMENTO.equals(p.getTipo())) {
            errores.add("Selecciona el tipo de producto.");
        }
        try {
            double precio = Double.parseDouble(request.getParameter("precio"));
            if (precio <= 0 || precio > 99999) {
                errores.add("El precio debe ser mayor a 0.");
            } else {
                p.setPrecio(Math.round(precio * 100.0) / 100.0);
            }
        } catch (RuntimeException e) {
            errores.add("El precio debe ser un número mayor a 0.");
        }

        if (!errores.isEmpty()) {
            request.setAttribute("tituloPagina", editando ? "Editar producto" : "Agregar producto");
            request.setAttribute("errores", errores);
            request.setAttribute("valores", p);
            request.setAttribute("editando", editando);
            request.getRequestDispatcher("/WEB-INF/views/productoForm.jsp").forward(request, response);
            return;
        }

        boolean ok = editando ? dao.update(p) : dao.create(p);
        if (ok) {
            List<String> lineas = new ArrayList<>();
            lineas.add(p.getNombre());
            lineas.add(p.getTipoTexto() + " · " + String.format(Locale.US, "$%,.2f MXN", p.getPrecio()));
            Flash.guardar(request, "modalTitulo", editando ? "Producto actualizado" : "Producto registrado");
            Flash.guardar(request, "modalLineas", lineas);
        } else {
            Flash.guardar(request, "error", "No se pudo guardar el producto. Inténtalo de nuevo.");
        }
        response.sendRedirect(request.getContextPath() + "/productos");
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean activo = "1".equals(request.getParameter("activo"));
            if (!dao.cambiarEstado(id, activo)) {
                Flash.guardar(request, "error", "No se pudo cambiar el estado del producto.");
            }
        } catch (NumberFormatException e) {
            Flash.guardar(request, "error", "Producto no válido.");
        }
        String q = request.getParameter("q") == null ? "" : request.getParameter("q").trim();
        response.sendRedirect(request.getContextPath() + "/productos?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8));
    }
}
