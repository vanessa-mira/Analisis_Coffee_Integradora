package mx.edu.utez.coffeeclub.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.utez.coffeeclub.model.dao.UsuarioDao;
import mx.edu.utez.coffeeclub.model.models.Usuario;
import mx.edu.utez.coffeeclub.utils.Flash;
import mx.edu.utez.coffeeclub.utils.PasswordUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Administrar usuarios (solo administrador): lista, alta de empleados y activar / desactivar cuentas. */
@WebServlet(name = "UsuarioServlet", value = "/usuarios")
public class UsuarioServlet extends HttpServlet {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\d{10}$");
    private static final Pattern PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");

    private final UsuarioDao dao = new UsuarioDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("action");

        if ("nuevo".equals(accion)) {
            request.setAttribute("tituloPagina", "Agregar empleado");
            Usuario nuevo = new Usuario();
            nuevo.setActivo(true); // por defecto la cuenta nace activa
            request.setAttribute("valores", nuevo);
            request.getRequestDispatcher("/WEB-INF/views/usuarioForm.jsp").forward(request, response);
            return;
        }

        String q = valor(request.getParameter("q"));
        String rol = valor(request.getParameter("rol"));
        String estado = valor(request.getParameter("estado"));

        Flash.pasar(request, "error", "modalTitulo", "modalLineas");
        request.setAttribute("tituloPagina", "Administrar Usuarios");
        request.setAttribute("usuarios", dao.buscar(q, rol, estado));
        request.setAttribute("q", q);
        request.setAttribute("rolFiltro", rol);
        request.setAttribute("estadoFiltro", estado);
        request.getRequestDispatcher("/WEB-INF/views/usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("action");
        if ("crear".equals(accion)) {
            crear(request, response);
        } else if ("estado".equals(accion)) {
            cambiarEstado(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/usuarios");
        }
    }

    private void crear(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = valor(request.getParameter("nombre"));
        String correo = valor(request.getParameter("correo"));
        String telefono = valor(request.getParameter("telefono"));
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");
        String confirmar = request.getParameter("confirmar") == null ? "" : request.getParameter("confirmar");
        boolean activo = request.getParameter("activo") != null;

        List<String> errores = new ArrayList<>();
        if (nombre.isEmpty()) errores.add("El nombre completo es obligatorio.");
        if (correo.isEmpty()) {
            errores.add("El correo electrónico es obligatorio.");
        } else if (!CORREO.matcher(correo).matches()) {
            errores.add("El correo electrónico no es válido.");
        } else if (dao.existeCorreo(correo, 0)) {
            errores.add("El correo electrónico ya está registrado.");
        }
        if (!telefono.isEmpty() && !TELEFONO.matcher(telefono).matches()) {
            errores.add("El teléfono debe tener 10 dígitos.");
        }
        if (!PASSWORD.matcher(password).matches()) {
            errores.add("La contraseña debe tener mínimo 8 caracteres, una letra y un número.");
        } else if (!password.equals(confirmar)) {
            errores.add("Las contraseñas no coinciden.");
        }

        if (!errores.isEmpty()) {
            Usuario valores = new Usuario();
            valores.setNombre(nombre);
            valores.setCorreo(correo);
            valores.setTelefono(telefono);
            valores.setActivo(activo);
            request.setAttribute("tituloPagina", "Agregar empleado");
            request.setAttribute("errores", errores);
            request.setAttribute("valores", valores);
            request.getRequestDispatcher("/WEB-INF/views/usuarioForm.jsp").forward(request, response);
            return;
        }

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setCorreo(correo);
        u.setTelefono(telefono.isEmpty() ? null : telefono);
        u.setPassword(PasswordUtil.hash(password));
        u.setRol(Usuario.EMPLEADO); // el administrador solo registra empleados
        u.setActivo(activo);

        if (dao.create(u)) {
            List<String> lineas = new ArrayList<>();
            lineas.add(nombre);
            lineas.add(correo);
            Flash.guardar(request, "modalTitulo", "Empleado agregado");
            Flash.guardar(request, "modalLineas", lineas);
        } else {
            Flash.guardar(request, "error", "No se pudo registrar al empleado. Inténtalo de nuevo.");
        }
        response.sendRedirect(request.getContextPath() + "/usuarios");
    }

    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario actual = (Usuario) request.getSession().getAttribute("usuario");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean activo = request.getParameter("activo") != null;

            if (id == actual.getIdUsuario() && !activo) {
                Flash.guardar(request, "error", "No puedes desactivar tu propia cuenta.");
            } else if (!dao.cambiarEstado(id, activo)) {
                Flash.guardar(request, "error", "No se pudo cambiar el estado de la cuenta.");
            }
        } catch (NumberFormatException e) {
            Flash.guardar(request, "error", "Usuario no válido.");
        }

        // Regresa a la lista conservando los filtros que había
        String destino = request.getContextPath() + "/usuarios?q=" + codificar(request.getParameter("q"))
                + "&rol=" + codificar(request.getParameter("rol"))
                + "&estado=" + codificar(request.getParameter("estado"));
        response.sendRedirect(destino);
    }

    private String valor(String texto) {
        return texto == null ? "" : texto.trim();
    }

    private String codificar(String texto) {
        return URLEncoder.encode(valor(texto), StandardCharsets.UTF_8);
    }
}
