/* The Coffee Club - comportamiento común de todas las pantallas */
(function () {
    var raiz = document.documentElement;

    // ---------- Tema claro / oscuro ----------
    function marcarBotones() {
        var oscuro = raiz.getAttribute('data-tema') === 'oscuro';
        document.querySelectorAll('.tema button[data-tema]').forEach(function (b) {
            b.classList.toggle('activo', (b.getAttribute('data-tema') === 'oscuro') === oscuro);
        });
    }

    function aplicarTema(tema) {
        if (tema === 'oscuro') {
            raiz.setAttribute('data-tema', 'oscuro');
        } else {
            raiz.removeAttribute('data-tema');
        }
        try { localStorage.setItem('cc-tema', tema); } catch (e) { /* sin almacenamiento */ }
        marcarBotones();
    }

    document.querySelectorAll('.tema button[data-tema]').forEach(function (b) {
        b.addEventListener('click', function () { aplicarTema(b.getAttribute('data-tema')); });
    });
    marcarBotones();

    // ---------- Menú lateral del administrador ----------
    var btnMenu = document.getElementById('btnMenu');
    var menu = document.getElementById('menuLateral');
    var fondo = document.getElementById('fondoMenu');

    function menuAbierto(abrir) {
        if (!menu) return;
        menu.classList.toggle('abierto', abrir);
        fondo.classList.toggle('abierto', abrir);
        if (btnMenu) btnMenu.setAttribute('aria-expanded', abrir ? 'true' : 'false');
    }

    if (btnMenu && menu) {
        btnMenu.addEventListener('click', function () { menuAbierto(!menu.classList.contains('abierto')); });
        fondo.addEventListener('click', function () { menuAbierto(false); });
    }

    // ---------- Formularios que se envían solos (filtros e interruptores) ----------
    document.querySelectorAll('[data-autoenviar]').forEach(function (el) {
        el.addEventListener('change', function () { el.form.submit(); });
    });

    // ---------- Ventana emergente ----------
    var modal = document.getElementById('modal');
    function cerrarModal() { if (modal) { modal.remove(); modal = null; } }
    if (modal) {
        var cerrar = document.getElementById('modalCerrar');
        if (cerrar) { cerrar.addEventListener('click', cerrarModal); cerrar.focus(); }
        modal.addEventListener('click', function (e) { if (e.target === modal) cerrarModal(); });
    }

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') { menuAbierto(false); cerrarModal(); }
    });
})();
