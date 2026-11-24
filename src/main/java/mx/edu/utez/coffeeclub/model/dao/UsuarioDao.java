package mx.edu.utez.coffeeclub.model.dao;

import mx.edu.utez.coffeeclub.DB.MySqlConnect;
import mx.edu.utez.coffeeclub.model.models.Usuario;
import mx.edu.utez.coffeeclub.utils.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao implements Dao<Usuario, Integer> {

    private static final String COLUMNAS =
            "u.id_usuario, u.nombre, u.correo, u.telefono, u.password, u.rol, u.activo, " +
            "(SELECT COUNT(*) FROM ventas v WHERE v.id_usuario = u.id_usuario) AS total_ventas";

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setTelefono(rs.getString("telefono"));
        u.setPassword(rs.getString("password"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        u.setTotalVentas(rs.getInt("total_ventas"));
        return u;
    }

    /** Devuelve el usuario si el correo y la contraseña son correctos (aunque esté inactivo). */
    public Usuario login(String correo, String password) {
        Usuario u = getByCorreo(correo);
        if (u != null && PasswordUtil.verificar(password, u.getPassword())) {
            return u;
        }
        return null;
    }

    public Usuario getByCorreo(String correo) {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios u WHERE u.correo = ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existeCorreo(String correo, int excluirId) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ? AND id_usuario <> ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override
    public boolean create(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, correo, telefono, password, rol, activo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRol());
            ps.setBoolean(6, u.isActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Usuario> getAll() {
        return buscar("", "", "");
    }

    /**
     * Lista de usuarios con filtros.
     * @param texto  busca en nombre o correo (vacío = todos)
     * @param rol    ADMINISTRADOR, EMPLEADO o vacío = todos
     * @param estado "1" activos, "0" inactivos o vacío = todos
     */
    public List<Usuario> buscar(String texto, String rol, String estado) {
        List<Usuario> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNAS + " FROM usuarios u WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {
            sql.append(" AND (u.nombre LIKE ? OR u.correo LIKE ?)");
            params.add("%" + texto.trim() + "%");
            params.add("%" + texto.trim() + "%");
        }
        if (rol != null && !rol.isBlank()) {
            sql.append(" AND u.rol = ?");
            params.add(rol);
        }
        if ("1".equals(estado) || "0".equals(estado)) {
            sql.append(" AND u.activo = ?");
            params.add("1".equals(estado));
        }
        sql.append(" ORDER BY u.rol ASC, u.nombre ASC");

        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Usuario getById(Integer id) {
        String sql = "SELECT " + COLUMNAS + " FROM usuarios u WHERE u.id_usuario = ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Actualiza los datos personales (nombre, correo y teléfono). */
    @Override
    public boolean update(Usuario u) {
        String sql = "UPDATE usuarios SET nombre = ?, correo = ?, telefono = ? WHERE id_usuario = ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getTelefono());
            ps.setInt(4, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Los usuarios no se borran: se desactivan para conservar su historial de ventas. */
    @Override
    public boolean delete(Integer id) {
        return cambiarEstado(id, false);
    }

    public boolean cambiarEstado(int id, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id_usuario = ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
