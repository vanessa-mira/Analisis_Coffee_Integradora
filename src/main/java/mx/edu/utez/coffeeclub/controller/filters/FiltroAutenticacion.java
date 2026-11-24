package mx.edu.utez.coffeeclub.controller.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.coffeeclub.model.models.Usuario;

import java.io.IOException;

/**
 * Protege todas las rutas: solo se puede entrar con sesión iniciada
 * y las pantallas del administrador no están disponibles para el empleado.
 */
@WebFilter(urlPatterns = {"/*"})
public class FiltroAutenticacion extends HttpFilter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Evita que el navegador guarde páginas (al cerrar sesión no se puede regresar con "atrás")
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String contextPath = request.getContextPath();
        String ruta = request.getRequestURI().substring(contextPath.length());

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuario") : null;
        boolean sesionIniciada = usuario != null;

        boolean esRecursoEstatico = ruta.startsWith("/css/") || ruta.startsWith("/js/") || ruta.startsWith("/img/");
        boolean esLogin = ruta.equals("/login");
        boolean esRaiz = ruta.isEmpty() || ruta.equals("/") || ruta.equals("/index.jsp");

        if (esRecursoEstatico) {
            chain.doFilter(req, res);
            return;
        }

        if (esRaiz) {
            response.sendRedirect(contextPath + (sesionIniciada ? inicioDe(usuario) : "/login"));
            return;
        }

        if (!sesionIniciada) {
            if (esLogin) {
                chain.doFilter(req, res);
            } else {
                response.sendRedirect(contextPath + "/login");
            }
            return;
        }

        // Con sesión iniciada no tiene sentido volver a ver el login
        if (esLogin) {
            response.sendRedirect(contextPath + inicioDe(usuario));
            return;
        }

        boolean esRutaAdmin = ruta.startsWith("/usuarios") || ruta.startsWith("/productos") || ruta.startsWith("/reportes");
        if (esRutaAdmin && !usuario.isAdministrador()) {
            response.sendRedirect(contextPath + "/venta");
            return;
        }

        chain.doFilter(req, res);
    }

    public static String inicioDe(Usuario usuario) {
        return usuario.isAdministrador() ? "/usuarios" : "/venta";
    }
}
