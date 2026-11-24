package mx.edu.utez.coffeeclub.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Mensajes de un solo uso: se guardan en la sesión antes de un redirect
 * y se pasan a la petición siguiente (para mostrar ventanas emergentes y alertas).
 */
public class Flash {

    public static void guardar(HttpServletRequest request, String clave, Object valor) {
        request.getSession().setAttribute("flash_" + clave, valor);
    }

    public static void pasar(HttpServletRequest request, String... claves) {
        HttpSession session = request.getSession(false);
        if (session == null) return;
        for (String clave : claves) {
            Object valor = session.getAttribute("flash_" + clave);
            if (valor != null) {
                request.setAttribute(clave, valor);
                session.removeAttribute("flash_" + clave);
            }
        }
    }
}
