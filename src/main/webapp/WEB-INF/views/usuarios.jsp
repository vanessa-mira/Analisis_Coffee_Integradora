<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menu" value="usuarios" scope="request"/>
<c:set var="accionTexto" value="Agregar empleado" scope="request"/>
<c:set var="accionUrl" value="/usuarios?action=nuevo" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<main class="contenedor">
    <c:if test="${not empty error}">
        <div class="alerta" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <form class="filtros" method="get" action="${ctx}/usuarios">
        <input class="entrada" type="search" name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre o correo..." aria-label="Buscar por nombre o correo">
        <select class="select" name="rol" data-autoenviar aria-label="Filtrar por rol">
            <option value="">Todos los roles</option>
            <option value="ADMINISTRADOR" <c:if test="${rolFiltro == 'ADMINISTRADOR'}">selected</c:if>>Administrador</option>
            <option value="EMPLEADO" <c:if test="${rolFiltro == 'EMPLEADO'}">selected</c:if>>Empleado</option>
        </select>
        <select class="select" name="estado" data-autoenviar aria-label="Filtrar por estado">
            <option value="">Todos los estados</option>
            <option value="1" <c:if test="${estadoFiltro == '1'}">selected</c:if>>Activo</option>
            <option value="0" <c:if test="${estadoFiltro == '0'}">selected</c:if>>Inactivo</option>
        </select>
    </form>

    <div class="tabla-caja">
        <table class="tabla">
            <thead>
            <tr>
                <th>Total de ventas</th>
                <th>Nombre completo</th>
                <th>Correo electrónico</th>
                <th>Rol</th>
                <th>Estado</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="u" items="${usuarios}">
                <tr>
                    <td class="suave">
                        <c:if test="${u.totalVentas > 0}">${u.totalVentas}</c:if>
                        <c:if test="${u.totalVentas == 0}">—</c:if>
                    </td>
                    <td class="nombre"><c:out value="${u.nombre}"/></td>
                    <td class="suave"><c:out value="${u.correo}"/></td>
                    <td>
                        <c:if test="${u.administrador}"><span class="insignia cuenta">Administrador</span></c:if>
                        <c:if test="${not u.administrador}"><span class="rol-caja">Empleado</span></c:if>
                    </td>
                    <td>
                        <form class="estado-celda" method="post" action="${ctx}/usuarios">
                            <input type="hidden" name="action" value="estado">
                            <input type="hidden" name="id" value="${u.idUsuario}">
                            <input type="hidden" name="q" value="<c:out value='${q}'/>">
                            <input type="hidden" name="rol" value="${rolFiltro}">
                            <input type="hidden" name="estado" value="${estadoFiltro}">
                            <label class="interruptor">
                                <input type="checkbox" name="activo" value="1" data-autoenviar
                                       <c:if test="${u.activo}">checked</c:if>
                                       <c:if test="${u.idUsuario == usr.idUsuario}">disabled</c:if>
                                       aria-label="Cuenta activa de <c:out value='${u.nombre}'/>">
                                <span class="pista"></span>
                                <span><c:if test="${u.activo}">Activo</c:if><c:if test="${not u.activo}">Inactivo</c:if></span>
                            </label>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty usuarios}">
                <tr><td colspan="5" class="vacio">No se encontraron usuarios con esos filtros.</td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>

<jsp:include page="layout/footer.jsp"/>
