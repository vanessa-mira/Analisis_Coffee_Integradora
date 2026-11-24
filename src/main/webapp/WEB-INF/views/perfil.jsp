<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menu" value="perfil" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<main class="perfil">
    <c:if test="${usr.administrador}"><a class="volver" href="${ctx}/usuarios">← Volver a usuarios</a></c:if>
    <c:if test="${not usr.administrador}"><a class="volver" href="${ctx}/venta">← Volver a ventas</a></c:if>

    <h2>Mi Perfil</h2>
    <p class="sub">Consulta y actualiza tus datos de acceso</p>

    <c:if test="${not empty error}">
        <div class="alerta" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${ctx}/perfil" class="perfil-caja" autocomplete="off">
        <div class="cuerpo">
            <h3>Datos personales</h3>
            <div class="perfil-datos">
                <div class="avatar" aria-hidden="true"><c:out value="${perfil.iniciales}"/></div>

                <div class="campo">
                    <label for="nombre">Nombre completo*</label>
                    <input class="entrada" type="text" id="nombre" name="nombre" maxlength="100" value="<c:out value='${perfil.nombre}'/>" required>
                </div>
                <div class="campo">
                    <label for="correo">Correo electrónico*</label>
                    <input class="entrada" type="email" id="correo" name="correo" maxlength="120" value="<c:out value='${perfil.correo}'/>" required>
                </div>
                <div class="campo">
                    <label for="rol">Rol en el sistema</label>
                    <input class="entrada" type="text" id="rol" value="${perfil.rolTexto}" readonly>
                </div>
                <div class="campo">
                    <label for="estado">Estado</label>
                    <input class="entrada" type="text" id="estado" value="<c:if test='${perfil.activo}'>Activo</c:if><c:if test='${not perfil.activo}'>Inactivo</c:if>" readonly>
                </div>
            </div>
        </div>
        <div class="perfil-pie">
            <button type="submit" class="btn">Guardar cambios</button>
        </div>
    </form>
</main>

<jsp:include page="layout/footer.jsp"/>
