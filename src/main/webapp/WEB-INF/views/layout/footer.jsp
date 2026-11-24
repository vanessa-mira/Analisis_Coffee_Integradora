<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<%-- Ventana emergente (venta registrada, producto registrado, empleado agregado...) --%>
<c:if test="${not empty modalTitulo}">
    <div class="modal-fondo" id="modal" role="dialog" aria-modal="true" aria-labelledby="modalEncabezado">
        <div class="modal">
            <div class="icono">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12.5l4.5 4.5L19 7.5"/></svg>
            </div>
            <h2 id="modalEncabezado"><c:out value="${modalTitulo}"/></h2>
            <c:forEach var="linea" items="${modalLineas}">
                <p><c:out value="${linea}"/></p>
            </c:forEach>
            <button type="button" class="btn" id="modalCerrar">Aceptar</button>
        </div>
    </div>
</c:if>

<script src="${ctx}/js/app.js"></script>
<c:if test="${not empty scriptPagina}">
    <script src="${ctx}/js/${scriptPagina}"></script>
</c:if>
</body>
</html>
