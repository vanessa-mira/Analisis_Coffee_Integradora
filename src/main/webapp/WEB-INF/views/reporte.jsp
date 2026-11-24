<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_US" scope="request"/>
<c:set var="menu" value="reportes" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<main class="contenedor">
    <div class="seccion-cabecera">
        <div>
            <h2>Resumen de ventas</h2>
            <p>Consulta los movimientos y el desempeño del equipo en un mismo lugar.</p>
        </div>
        <span class="ayuda">Importes en MXN</span>
    </div>

    <c:if test="${not empty error}">
        <div class="alerta" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <section class="filtro-fechas" aria-label="Filtrar por fechas">
        <h3>Filtrar por fechas</h3>
        <form method="get" action="${ctx}/reportes">
            <div class="campo">
                <label for="inicio">Fecha de inicio *</label>
                <input class="entrada" type="date" id="inicio" name="inicio" value="${inicio}" required>
            </div>
            <div class="campo">
                <label for="fin">Fecha de fin *</label>
                <input class="entrada" type="date" id="fin" name="fin" value="${fin}" required>
            </div>
            <button type="submit" class="btn">Aplicar filtro</button>
            <a class="btn btn-secundario" href="${ctx}/reportes">Limpiar filtro</a>
        </form>
    </section>

    <div class="tarjetas-resumen">
        <div class="dato destacado">
            <div class="etiqueta">Total recaudado</div>
            <div class="valor">$<fmt:formatNumber value="${totalRecaudado}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="nota">MXN · <c:if test="${empty inicio}">Todo el historial</c:if><c:if test="${not empty inicio}">Período seleccionado</c:if></div>
        </div>
        <div class="dato">
            <div class="etiqueta">Ventas registradas</div>
            <div class="valor">${numVentas}</div>
        </div>
        <div class="dato">
            <div class="etiqueta">Empleados con ventas</div>
            <div class="valor">${numEmpleados}</div>
            <div class="nota"><c:out value="${notaInactivas}"/></div>
        </div>
    </div>

    <div class="dos-columnas">
        <section class="panel-tabla" aria-label="Historial de ventas">
            <div class="encabezado">
                <h3>Historial de ventas</h3>
                <p>${numVentas} <c:if test="${numVentas == 1}">venta</c:if><c:if test="${numVentas != 1}">ventas</c:if> · más recientes primero</p>
            </div>
            <table class="tabla">
                <thead>
                <tr><th>Fecha y hora</th><th>Empleado</th><th>Método de pago</th><th class="derecha">Total</th></tr>
                </thead>
                <tbody>
                <c:forEach var="v" items="${ventas}">
                    <tr>
                        <td>${v.fechaTexto}</td>
                        <td class="nombre"><c:out value="${v.nombreEmpleado}"/></td>
                        <td>${v.metodoPagoTexto}</td>
                        <td class="derecha">$<fmt:formatNumber value="${v.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty ventas}">
                    <tr><td colspan="4" class="vacio">No hay ventas en el período seleccionado.</td></tr>
                </c:if>
                </tbody>
            </table>
            <div class="paginacion">
                <span>Mostrando ${desde}–${hasta} de ${numVentas} ventas</span>
                <div class="paginas">
                    <a href="${ctx}/reportes?pagina=${pagina - 1}${filtro}" class="${pagina <= 1 ? 'deshabilitado' : ''}">Anterior</a>
                    <c:forEach var="i" begin="1" end="${totalPaginas}">
                        <a href="${ctx}/reportes?pagina=${i}${filtro}" class="${i == pagina ? 'actual' : ''}">${i}</a>
                    </c:forEach>
                    <a href="${ctx}/reportes?pagina=${pagina + 1}${filtro}" class="${pagina >= totalPaginas ? 'deshabilitado' : ''}">Siguiente</a>
                </div>
            </div>
        </section>

        <section class="panel-tabla" aria-label="Ventas por empleado">
            <div class="encabezado">
                <h3>Ventas por empleado</h3>
                <p>Desglose del mismo período</p>
            </div>
            <table class="tabla">
                <thead>
                <tr><th>Empleado</th><th class="centro">Ventas</th><th class="derecha">Total</th></tr>
                </thead>
                <tbody>
                <c:forEach var="r" items="${porEmpleado}">
                    <tr>
                        <td class="nombre"><c:out value="${r.nombre}"/><c:if test="${not r.activo}"><small>Cuenta inactiva</small></c:if></td>
                        <td class="centro">${r.ventas}</td>
                        <td class="derecha">$<fmt:formatNumber value="${r.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty porEmpleado}">
                    <tr><td colspan="3" class="vacio">Sin ventas en el período.</td></tr>
                </c:if>
                </tbody>
                <tfoot>
                <tr>
                    <td>Total del período</td>
                    <td class="centro">${totalVentasEmpleados}</td>
                    <td class="derecha">$<fmt:formatNumber value="${totalRecaudado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
                </tfoot>
            </table>
        </section>
    </div>

    <p class="nota-pie"><c:out value="${periodoTexto}"/></p>
</main>

<jsp:include page="layout/footer.jsp"/>
