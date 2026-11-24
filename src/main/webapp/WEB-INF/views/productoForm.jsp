<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menu" value="productos" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<main class="contenedor">
    <a class="volver" href="${ctx}/productos">← Volver al catálogo</a>

    <div class="form-titulo">
        <div>
            <h2><c:if test="${editando}">Editar producto</c:if><c:if test="${not editando}">Nuevo producto</c:if></h2>
            <p>Completa los datos del alimento o la bebida.</p>
        </div>
        <span class="ayuda">* Campos obligatorios</span>
    </div>

    <c:if test="${not empty errores}">
        <div class="alerta" role="alert">
            <ul>
                <c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach>
            </ul>
        </div>
    </c:if>

    <form method="post" action="${ctx}/productos" autocomplete="off" novalidate>
        <input type="hidden" name="action" value="${editando ? 'actualizar' : 'crear'}">
        <c:if test="${editando}"><input type="hidden" name="id" value="${valores.idProducto}"></c:if>

        <div class="form-caja">
            <div class="caja-cabecera">Datos del producto</div>
            <div class="caja-cuerpo">
                <div class="campo">
                    <label for="nombre">Nombre del producto *</label>
                    <input class="entrada" type="text" id="nombre" name="nombre" maxlength="100" placeholder="Ej. Capuccino" value="<c:out value='${valores.nombre}'/>" required>
                </div>
                <div class="campo">
                    <label for="tipo">Tipo *</label>
                    <select class="select" id="tipo" name="tipo" required>
                        <option value="">Selecciona una opción</option>
                        <option value="BEBIDA" <c:if test="${valores.tipo == 'BEBIDA'}">selected</c:if>>Bebida</option>
                        <option value="ALIMENTO" <c:if test="${valores.tipo == 'ALIMENTO'}">selected</c:if>>Alimento</option>
                    </select>
                </div>
                <div class="campo">
                    <label for="precio">Precio (MXN) *</label>
                    <input class="entrada" type="number" id="precio" name="precio" min="0.01" step="0.01" inputmode="decimal" placeholder="0.00" value="<c:if test='${valores.precio > 0}'>${valores.precio}</c:if>" required>
                </div>
                <div class="campo completo">
                    <label for="descripcion">Descripción</label>
                    <textarea class="area" id="descripcion" name="descripcion" maxlength="500" placeholder="Información general del producto"><c:out value="${valores.descripcion}"/></textarea>
                </div>
            </div>
        </div>

        <div class="form-pie">
            <span></span>
            <div class="botones">
                <a class="btn btn-secundario" href="${ctx}/productos">Cancelar</a>
                <button type="submit" class="btn"><c:if test="${editando}">Guardar cambios</c:if><c:if test="${not editando}">Agregar producto</c:if></button>
            </div>
        </div>
    </form>
</main>

<jsp:include page="layout/footer.jsp"/>
