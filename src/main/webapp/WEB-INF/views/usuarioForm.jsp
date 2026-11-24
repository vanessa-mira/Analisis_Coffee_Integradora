<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="menu" value="usuarios" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<main class="contenedor">
    <a class="volver" href="${ctx}/usuarios">← Volver a usuarios</a>

    <div class="form-titulo">
        <div>
            <h2>Nuevo empleado</h2>
            <p>Completa los datos para crear una cuenta de acceso a la cafetería.</p>
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

    <form method="post" action="${ctx}/usuarios" autocomplete="off" novalidate>
        <input type="hidden" name="action" value="crear">

        <div class="form-caja">
            <div class="caja-cabecera">Datos del empleado</div>
            <div class="caja-cuerpo">
                <div class="campo">
                    <label for="nombre">Nombre completo *</label>
                    <input class="entrada" type="text" id="nombre" name="nombre" maxlength="100" placeholder="Escribe el nombre completo" value="<c:out value='${valores.nombre}'/>" required>
                </div>
                <div class="campo">
                    <label for="correo">Correo electrónico *</label>
                    <input class="entrada" type="email" id="correo" name="correo" maxlength="120" placeholder="nombre@correo.com" value="<c:out value='${valores.correo}'/>" required>
                </div>
                <div class="campo">
                    <label for="telefono">Teléfono</label>
                    <input class="entrada" type="tel" id="telefono" name="telefono" maxlength="10" inputmode="numeric" pattern="[0-9]{10}" placeholder="Número de 10 dígitos" value="<c:out value='${valores.telefono}'/>">
                </div>
                <div class="campo">
                    <label for="rol">Rol</label>
                    <input class="entrada" type="text" id="rol" value="Empleado" readonly>
                </div>

                <h4>Acceso a la cuenta</h4>

                <div class="campo">
                    <label for="password">Contraseña *</label>
                    <input class="entrada" type="password" id="password" name="password" placeholder="Crea una contraseña" autocomplete="new-password" required>
                </div>
                <div class="campo">
                    <label for="confirmar">Confirmar contraseña *</label>
                    <input class="entrada" type="password" id="confirmar" name="confirmar" placeholder="Repite la contraseña" autocomplete="new-password" required>
                </div>
                <p class="ayuda completo">Mínimo 8 caracteres, una letra y un número.</p>
            </div>
        </div>

        <div class="form-pie">
            <div class="estado">
                <div>
                    <strong>Estado del empleado</strong>
                    <small>Podrá iniciar sesión en cuanto guardes su cuenta.</small>
                </div>
                <label class="interruptor">
                    <input type="checkbox" name="activo" value="1" <c:if test="${valores.activo}">checked</c:if>>
                    <span class="pista"></span>
                    <span>Activo</span>
                </label>
            </div>
            <div class="botones">
                <a class="btn btn-secundario" href="${ctx}/usuarios">Cancelar</a>
                <button type="submit" class="btn">Agregar empleado</button>
            </div>
        </div>
    </form>
</main>

<jsp:include page="layout/footer.jsp"/>
