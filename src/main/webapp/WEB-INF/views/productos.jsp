<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_US" scope="request"/>
<c:set var="menu" value="productos" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<main class="contenedor">
    <c:if test="${not empty error}">
        <div class="alerta" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <div class="seccion-cabecera">
        <div>
            <h2>Productos</h2>
            <p>Consulta el catálogo y administra la disponibilidad de cada producto.</p>
        </div>
        <a class="btn" href="${ctx}/productos?action=nuevo">Agregar producto</a>
    </div>

    <form method="get" action="${ctx}/productos">
        <input class="entrada" type="search" name="q" value="<c:out value='${q}'/>" placeholder="Buscar por nombre de producto…" aria-label="Buscar por nombre de producto">
    </form>

    <%-- Productos disponibles --%>
    <section class="seccion">
        <div class="seccion-cabecera">
            <div>
                <h3>Productos disponibles</h3>
                <p class="chico">Se muestran en el punto de venta y pueden venderse.</p>
            </div>
            <span class="insignia">${disponibles.size()} <c:if test="${disponibles.size() == 1}">producto</c:if><c:if test="${disponibles.size() != 1}">productos</c:if></span>
        </div>
        <div class="tabla-caja">
            <table class="tabla">
                <thead>
                <tr><th>Nombre</th><th>Tipo</th><th>Precio</th><th>Estado</th><th>Acción</th></tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${disponibles}">
                    <tr>
                        <td class="nombre"><c:out value="${p.nombre}"/></td>
                        <td class="suave">${p.tipoTexto}</td>
                        <td>$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/> MXN</td>
                        <td><span class="insignia ok">Disponible</span></td>
                        <td>
                            <div class="acciones-fila">
                                <form method="post" action="${ctx}/productos">
                                    <input type="hidden" name="action" value="estado">
                                    <input type="hidden" name="id" value="${p.idProducto}">
                                    <input type="hidden" name="activo" value="0">
                                    <input type="hidden" name="q" value="<c:out value='${q}'/>">
                                    <button type="submit" class="btn btn-secundario btn-chico">Inhabilitar</button>
                                </form>
                                <a class="btn btn-secundario btn-chico" href="${ctx}/productos?action=editar&id=${p.idProducto}">Editar</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty disponibles}">
                    <tr><td colspan="5" class="vacio">No hay productos disponibles.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>

    <%-- Productos inhabilitados --%>
    <section class="seccion">
        <div class="seccion-cabecera">
            <div>
                <h3>Productos inhabilitados</h3>
                <p class="chico">No se muestran en el punto de venta. Puedes volver a habilitarlos.</p>
            </div>
            <span class="insignia">${inhabilitados.size()} <c:if test="${inhabilitados.size() == 1}">producto</c:if><c:if test="${inhabilitados.size() != 1}">productos</c:if></span>
        </div>
        <div class="tabla-caja">
            <table class="tabla">
                <thead>
                <tr><th>Nombre</th><th>Tipo</th><th>Precio</th><th>Estado</th><th>Acción</th></tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${inhabilitados}">
                    <tr>
                        <td class="nombre"><c:out value="${p.nombre}"/></td>
                        <td class="suave">${p.tipoTexto}</td>
                        <td>$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/> MXN</td>
                        <td><span class="insignia gris">Inhabilitado</span></td>
                        <td>
                            <div class="acciones-fila">
                                <form method="post" action="${ctx}/productos">
                                    <input type="hidden" name="action" value="estado">
                                    <input type="hidden" name="id" value="${p.idProducto}">
                                    <input type="hidden" name="activo" value="1">
                                    <input type="hidden" name="q" value="<c:out value='${q}'/>">
                                    <button type="submit" class="btn btn-secundario btn-chico">Habilitar</button>
                                </form>
                                <a class="btn btn-secundario btn-chico" href="${ctx}/productos?action=editar&id=${p.idProducto}">Editar</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty inhabilitados}">
                    <tr><td colspan="5" class="vacio">No hay productos inhabilitados.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>
</main>

<jsp:include page="layout/footer.jsp"/>
