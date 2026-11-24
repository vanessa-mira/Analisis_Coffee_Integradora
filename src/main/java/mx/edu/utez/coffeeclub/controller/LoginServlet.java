package mx.edu.utez.coffeeclub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.edu.utez.coffeeclub.controller.filters.FiltroAutenticacion;
import mx.edu.utez.coffeeclub.model.dao.UsuarioDao;
import mx.edu.utez.coffeeclub.model.models.Usuario;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDao dao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = request.getParameter("correo");
        String contra = request.getParameter("contra");

        if (correo == null || correo.isBlank() || contra == null || contra.isEmpty()) {
            volverConError(request, response, "Escribe tu correo electrónico y tu contraseña.", correo);
            return;
        }

        Usuario usuario = dao.login(correo.trim(), contra);

        if (usuario == null) {
            volverConError(request, response, "Los datos ingresados son incorrectos.", correo);
        } else if (!usuario.isActivo()) {
            volverConError(request, response, "Tu cuenta está inactiva. Habla con el administrador.", correo);
        } else {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            response.sendRedirect(request.getContextPath() + FiltroAutenticacion.inicioDe(usuario));
        }
    }

    private void volverConError(HttpServletRequest request, HttpServletResponse response,
                                String mensaje, String correo) throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.setAttribute("correo", correo == null ? "" : correo.trim());
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}
