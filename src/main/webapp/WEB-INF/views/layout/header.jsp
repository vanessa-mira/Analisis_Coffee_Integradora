<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="usr" value="${sessionScope.usuario}" scope="request"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${tituloPagina} - The Coffee Club</title>
    <link rel="icon" type="image/png" href="${ctx}/img/favicon.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/style.css">
    <script>
        /* Aplica el tema guardado antes de pintar la página (evita el parpadeo) */
        try { if (localStorage.getItem('cc-tema') === 'oscuro') document.documentElement.setAttribute('data-tema', 'oscuro'); } catch (e) {}
    </script>
</head>
<body>

<header class="barra">
    <div class="barra-izq">
        <a class="marca" href="${ctx}/">
            <img class="logo-mini" src="${ctx}/img/logo.png" alt="The Coffee Club">
            <c:if test="${not usr.administrador}">
                <span class="titulo-barra">The Coffee Club</span>
            </c:if>
        </a>
        <c:if test="${usr.administrador}">
            <button type="button" class="btn-menu" id="btnMenu" aria-label="Abrir menú" aria-expanded="false">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"><path d="M3 6h18M3 12h18M3 18h18"/></svg>
            </button>
            <h1 class="titulo-barra">${tituloPagina}</h1>
        </c:if>
    </div>

    <div class="barra-der">
        <div class="tema" role="group" aria-label="Tema de la página">
            <button type="button" data-tema="claro" class="activo">Claro</button>
            <button type="button" data-tema="oscuro">Oscuro</button>
        </div>
        <c:if test="${not empty accionTexto}">
            <a class="btn" href="${ctx}${accionUrl}"><c:out value="${accionTexto}"/></a>
        </c:if>
        <a class="pill-usuario" href="${ctx}/perfil" title="Mi perfil">
            <c:if test="${usr.administrador}">Admin: </c:if><c:if test="${not usr.administrador}">Empleado: </c:if><c:out value="${usr.primerNombre}"/>
        </a>
        <a class="btn-salir" href="${ctx}/logout">Cerrar sesión</a>
    </div>
</header>

<c:if test="${usr.administrador}">
    <div class="fondo-menu" id="fondoMenu"></div>
    <nav class="menu-lateral" id="menuLateral" aria-label="Menú principal">
        <div class="menu-cabecera">
            <img class="logo-mini" src="${ctx}/img/logo.png" alt="">
            <span>The Coffee Club</span>
        </div>
        <a href="${ctx}/venta" class="${menu == 'venta' ? 'actual' : ''}">Punto de venta</a>
        <a href="${ctx}/usuarios" class="${menu == 'usuarios' ? 'actual' : ''}">Usuarios</a>
        <a href="${ctx}/productos" class="${menu == 'productos' ? 'actual' : ''}">Catálogo de productos</a>
        <a href="${ctx}/reportes" class="${menu == 'reportes' ? 'actual' : ''}">Historial de ventas</a>
        <a href="${ctx}/perfil" class="${menu == 'perfil' ? 'actual' : ''}">Mi perfil</a>
    </nav>
</c:if>
