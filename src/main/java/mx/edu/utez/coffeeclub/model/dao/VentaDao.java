package mx.edu.utez.coffeeclub.model.dao;

import mx.edu.utez.coffeeclub.DB.MySqlConnect;
import mx.edu.utez.coffeeclub.model.models.DetalleVenta;
import mx.edu.utez.coffeeclub.model.models.ResumenEmpleado;
import mx.edu.utez.coffeeclub.model.models.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDao implements Dao<Venta, Integer> {

    /** Guarda la venta y todos sus productos en una sola transacción. */
    @Override
    public boolean create(Venta v) {
        String sqlVenta = "INSERT INTO ventas (fecha, total, metodo_pago, monto_recibido, cambio, id_usuario) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalles_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = MySqlConnect.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {

                psVenta.setTimestamp(1, Timestamp.valueOf(v.getFecha()));
                psVenta.setDouble(2, v.getTotal());
                psVenta.setString(3, v.getMetodoPago());
                psVenta.setObject(4, v.getMontoRecibido());
                psVenta.setObject(5, v.getCambio());
                psVenta.setInt(6, v.getIdUsuario());
                psVenta.executeUpdate();

                try (ResultSet keys = psVenta.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se obtuvo el id de la venta");
                    v.setIdVenta(keys.getInt(1));
                }

                for (DetalleVenta d : v.getDetalles()) {
                    psDetalle.setInt(1, v.getIdVenta());
                    psDetalle.setInt(2, d.getIdProducto());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setDouble(4, d.getPrecioUnitario());
                    psDetalle.setDouble(5, d.getSubtotal());
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Venta> getAll() {
        return listar(null, null, 0, Integer.MAX_VALUE);
    }

    @Override
    public Venta getById(Integer id) {
        String sql = "SELECT v.id_venta, v.fecha, v.total, v.metodo_pago, v.monto_recibido, v.cambio, " +
                "v.id_usuario, u.nombre FROM ventas v JOIN usuarios u ON u.id_usuario = v.id_usuario " +
                "WHERE v.id_venta = ?";
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

    /** Las ventas registradas no se modifican ni se eliminan. */
    @Override
    public boolean update(Venta entidad) {
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    // ---------------------- Reportes (Historial de ventas) ----------------------

    private Venta mapear(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setIdVenta(rs.getInt("id_venta"));
        v.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        v.setTotal(rs.getDouble("total"));
        v.setMetodoPago(rs.getString("metodo_pago"));
        double monto = rs.getDouble("monto_recibido");
        v.setMontoRecibido(rs.wasNull() ? null : monto);
        double cambio = rs.getDouble("cambio");
        v.setCambio(rs.wasNull() ? null : cambio);
        v.setIdUsuario(rs.getInt("id_usuario"));
        v.setNombreEmpleado(rs.getString("nombre"));
        return v;
    }

    /** Condición de fechas: el rango incluye el día de inicio y el día de fin completos. */
    private String filtroFechas(LocalDate inicio, LocalDate fin) {
        StringBuilder sb = new StringBuilder();
        if (inicio != null) sb.append(" AND v.fecha >= ?");
        if (fin != null) sb.append(" AND v.fecha < ?");
        return sb.toString();
    }

    private int ponerFechas(PreparedStatement ps, LocalDate inicio, LocalDate fin, int indice) throws SQLException {
        if (inicio != null) ps.setTimestamp(indice++, Timestamp.valueOf(inicio.atStartOfDay()));
        if (fin != null) ps.setTimestamp(indice++, Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));
        return indice;
    }

    public double totalRecaudado(LocalDate inicio, LocalDate fin) {
        String sql = "SELECT COALESCE(SUM(v.total), 0) FROM ventas v WHERE 1 = 1" + filtroFechas(inicio, fin);
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ponerFechas(ps, inicio, fin, 1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int contarVentas(LocalDate inicio, LocalDate fin) {
        String sql = "SELECT COUNT(*) FROM ventas v WHERE 1 = 1" + filtroFechas(inicio, fin);
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ponerFechas(ps, inicio, fin, 1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Ventas del periodo, de la más reciente a la más antigua, con paginación. */
    public List<Venta> listar(LocalDate inicio, LocalDate fin, int offset, int limite) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha, v.total, v.metodo_pago, v.monto_recibido, v.cambio, " +
                "v.id_usuario, u.nombre FROM ventas v JOIN usuarios u ON u.id_usuario = v.id_usuario " +
                "WHERE 1 = 1" + filtroFechas(inicio, fin) + " ORDER BY v.fecha DESC LIMIT ? OFFSET ?";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            int i = ponerFechas(ps, inicio, fin, 1);
            ps.setInt(i++, limite);
            ps.setInt(i, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Desglose de ventas por empleado (incluye empleados con cuenta inactiva). */
    public List<ResumenEmpleado> porEmpleado(LocalDate inicio, LocalDate fin) {
        List<ResumenEmpleado> lista = new ArrayList<>();
        String sql = "SELECT u.nombre, u.activo, COUNT(*) AS ventas, SUM(v.total) AS total " +
                "FROM ventas v JOIN usuarios u ON u.id_usuario = v.id_usuario " +
                "WHERE 1 = 1" + filtroFechas(inicio, fin) +
                " GROUP BY u.id_usuario, u.nombre, u.activo ORDER BY total DESC";
        try (Connection con = MySqlConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ponerFechas(ps, inicio, fin, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResumenEmpleado r = new ResumenEmpleado();
                    r.setNombre(rs.getString("nombre"));
                    r.setActivo(rs.getBoolean("activo"));
                    r.setVentas(rs.getInt("ventas"));
                    r.setTotal(rs.getDouble("total"));
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
