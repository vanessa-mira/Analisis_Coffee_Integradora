package mx.edu.utez.coffeeclub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.coffeeclub.model.dao.VentaDao;
import mx.edu.utez.coffeeclub.model.models.ResumenEmpleado;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/** Historial de ventas (solo administrador): filtro por fechas, total recaudado y ventas por empleado. */
@WebServlet(name = "ReporteServlet", value = "/reportes")
public class ReporteServlet extends HttpServlet {

    private static final int POR_PAGINA = 5;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final VentaDao dao = new VentaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String inicioTxt = request.getParameter("inicio") == null ? "" : request.getParameter("inicio").trim();
        String finTxt = request.getParameter("fin") == null ? "" : request.getParameter("fin").trim();
        LocalDate inicio = null;
        LocalDate fin = null;

        // Sin fechas se muestra todo el historial
        if (!inicioTxt.isEmpty() || !finTxt.isEmpty()) {
            try {
                if (inicioTxt.isEmpty() || finTxt.isEmpty()) {
                    throw new IllegalArgumentException("Selecciona la fecha de inicio y la fecha de fin.");
                }
                inicio = LocalDate.parse(inicioTxt);
                fin = LocalDate.parse(finTxt);
                if (fin.isBefore(inicio)) {
                    throw new IllegalArgumentException("El rango de fechas no es válido.");
                }
            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Las fechas no son válidas.");
                inicio = null;
                fin = null;
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());
                inicio = null;
                fin = null;
            }
        }

        int numVentas = dao.contarVentas(inicio, fin);
        int totalPaginas = Math.max(1, (int) Math.ceil(numVentas / (double) POR_PAGINA));
        int pagina = 1;
        try {
            pagina = Integer.parseInt(request.getParameter("pagina"));
        } catch (NumberFormatException e) {
            // pagina 1
        }
        pagina = Math.min(Math.max(pagina, 1), totalPaginas);
        int offset = (pagina - 1) * POR_PAGINA;

        List<ResumenEmpleado> porEmpleado = dao.porEmpleado(inicio, fin);
        long inactivas = porEmpleado.stream().filter(r -> !r.isActivo()).count();
        int totalVentasEmpleados = porEmpleado.stream().mapToInt(ResumenEmpleado::getVentas).sum();

        String periodo = (inicio == null)
                ? "Período consultado: todo el historial."
                : "Período consultado: del " + inicio.format(FORMATO) + " al " + fin.format(FORMATO) + ", ambas fechas incluidas.";

        request.setAttribute("tituloPagina", "Historial de ventas");
        request.setAttribute("inicio", inicio == null ? "" : inicio.toString());
        request.setAttribute("fin", fin == null ? "" : fin.toString());
        request.setAttribute("totalRecaudado", dao.totalRecaudado(inicio, fin));
        request.setAttribute("numVentas", numVentas);
        request.setAttribute("numEmpleados", porEmpleado.size());
        request.setAttribute("notaInactivas", inactivas == 0 ? "" : "Incluye " + inactivas + (inactivas == 1 ? " cuenta inactiva" : " cuentas inactivas"));
        request.setAttribute("ventas", dao.listar(inicio, fin, offset, POR_PAGINA));
        request.setAttribute("porEmpleado", porEmpleado);
        request.setAttribute("totalVentasEmpleados", totalVentasEmpleados);
        request.setAttribute("pagina", pagina);
        request.setAttribute("totalPaginas", totalPaginas);
        request.setAttribute("desde", numVentas == 0 ? 0 : offset + 1);
        request.setAttribute("hasta", Math.min(offset + POR_PAGINA, numVentas));
        request.setAttribute("periodoTexto", periodo);
        request.setAttribute("filtro", inicio == null ? "" : "&inicio=" + inicio + "&fin=" + fin);
        request.getRequestDispatcher("/WEB-INF/views/reporte.jsp").forward(request, response);
    }
}
