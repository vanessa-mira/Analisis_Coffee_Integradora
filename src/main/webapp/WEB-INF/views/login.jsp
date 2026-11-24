<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión - The Coffee Club</title>
    <link rel="icon" type="image/png" href="${ctx}/img/favicon.png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/css/style.css">
    <script>
        try { if (localStorage.getItem('cc-tema') === 'oscuro') document.documentElement.setAttribute('data-tema', 'oscuro'); } catch (e) {}
    </script>
</head>
<body>
<main class="login-pagina">
    <img class="login-logo" src="${ctx}/img/logo.png" alt="The Coffee Club">

    <div class="login-tarjeta">
        <c:if test="${not empty error}">
            <div class="alerta" role="alert"><c:out value="${error}"/></div>
        </c:if>

        <form action="${ctx}/login" method="post" autocomplete="on">
            <div class="campo">
                <label for="correo">Correo electrónico</label>
                <input class="entrada" type="email" id="correo" name="correo" value="<c:out value='${correo}'/>" required autofocus>
            </div>
            <div class="campo">
                <label for="contra">Contraseña</label>
                <input class="entrada" type="password" id="contra" name="contra" required>
            </div>
            <div class="login-boton">
                <button type="submit" class="btn">
                    <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 4h4a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-4M3 12h12m0 0l-4-4m4 4l-4 4"/></svg>
                    Iniciar sesion
                </button>
            </div>
        </form>
    </div>
</main>
</body>
</html>
