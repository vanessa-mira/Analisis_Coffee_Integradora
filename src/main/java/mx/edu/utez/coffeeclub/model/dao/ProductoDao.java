package mx.edu.utez.coffeeclub.model.dao;

import mx.edu.utez.coffeeclub.DB.MySqlConnect;
import mx.edu.utez.coffeeclub.model.models.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao implements Dao<Producto, Integer> {

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setTipo(rs.getString("tipo"));
        p.setPrecio(rs.getDouble("precio"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    @Override
    public boolean create(Producto p) {
        String sql = "INSERT INTO productos (nombre, tipo, precio, descripcion, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTipo());
            ps.setDouble(3, p.getPrecio());
            ps.setString(4, p.getDescripcion());
            ps.setBoolean(5, p.isActivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Producto> getAll() {
        return buscar("", null);
    }

    /** Productos que se muestran en el punto de venta. */
    public List<Producto> getActivos() {
        return buscar("", true);
    }

    /**
     * @param texto  busca por nombre (vacío = todos)
     * @param activo true = solo activos, false = solo inhabilitados, null = todos
     */
    public List<Producto> buscar(String texto, Boolean activo) {
        List<Producto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id_producto, nombre, tipo, precio, descripcion, activo FROM productos WHERE 1 = 1");
        List<Object> params = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {
            sql.append(" AND nombre LIKE ?");
            params.add("%" + texto.trim() + "%");
        }
        if (activo != null) {
            sql.append(" AND activo = ?");
            params.add(activo);
        }
        sql.append(" ORDER BY tipo ASC, nombre ASC");

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
    public Producto getById(Integer id) {
        String sql = "SELECT id_producto, nombre, tipo, precio, descripcion, activo FROM productos WHERE id_producto = ?";
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

    public boolean existeNombre(String nombre, int excluirId) {
        String sql = "SELECT COUNT(*) FROM productos WHERE nombre = ? AND id_producto <> ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
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
    public boolean update(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, tipo = ?, precio = ?, descripcion = ? WHERE id_producto = ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getTipo());
            ps.setDouble(3, p.getPrecio());
            ps.setString(4, p.getDescripcion());
            ps.setInt(5, p.getIdProducto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Los productos no se borran: se inhabilitan para conservar las ventas anteriores. */
    @Override
    public boolean delete(Integer id) {
        return cambiarEstado(id, false);
    }

    public boolean cambiarEstado(int id, boolean activo) {
        String sql = "UPDATE productos SET activo = ? WHERE id_producto = ?";
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
