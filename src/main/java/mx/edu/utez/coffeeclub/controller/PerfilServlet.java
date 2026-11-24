package mx.edu.utez.coffeeclub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.coffeeclub.model.dao.UsuarioDao;
import mx.edu.utez.coffeeclub.model.models.Usuario;
import mx.edu.utez.coffeeclub.utils.Flash;

import java.io.IOException;
import java.util.regex.Pattern;

/** Mi perfil: el usuario consulta y actualiza su nombre y su correo. */
@WebServlet(name = "PerfilServlet", value = "/perfil")
public class PerfilServlet extends HttpServlet {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioDao dao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario actual = (Usuario) request.getSession().getAttribute("usuario");
        Flash.pasar(request, "error", "modalTitulo", "modalLineas");
        request.setAttribute("tituloPagina", "Mi perfil");
        request.setAttribute("perfil", dao.getById(actual.getIdUsuario()));
        request.getRequestDispatcher("/WEB-INF/views/perfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario actual = (Usuario) request.getSession().getAttribute("usuario");
        String nombre = request.getParameter("nombre") == null ? "" : request.getParameter("nombre").trim();
        String correo = request.getParameter("correo") == null ? "" : request.getParameter("correo").trim();

        if (nombre.isEmpty() || !CORREO.matcher(correo).matches()) {
            Flash.guardar(request, "error", "Escribe tu nombre y un correo electrónico válido.");
        } else if (dao.existeCorreo(correo, actual.getIdUsuario())) {
            Flash.guardar(request, "error", "Ese correo electrónico ya está registrado.");
        } else {
            Usuario fresco = dao.getById(actual.getIdUsuario());
            fresco.setNombre(nombre);
            fresco.setCorreo(correo);
            if (dao.update(fresco)) {
                request.getSession().setAttribute("usuario", dao.getById(actual.getIdUsuario()));
                Flash.guardar(request, "modalTitulo", "Perfil actualizado");
            } else {
                Flash.guardar(request, "error", "No se pudo actualizar tu perfil.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/perfil");
    }
}
