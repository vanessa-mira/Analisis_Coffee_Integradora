/* Punto de venta: pedido, total, pago y cambio (el servidor vuelve a calcular todo al registrar) */
(function () {
    var pedido = {};           // id -> { nombre, precio, cantidad }
    var metodo = 'EFECTIVO';

    var tarjetas = document.querySelectorAll('.producto');
    var lista = document.getElementById('pedidoLista');
    var vacio = document.getElementById('pedidoVacio');
    var subtotalEl = document.getElementById('subtotal');
    var totalEl = document.getElementById('total');
    var cambioEl = document.getElementById('cambio');
    var monto = document.getElementById('montoRecibido');
    var btnRegistrar = document.getElementById('btnRegistrar');
    var inputItems = document.getElementById('items');
    var inputMetodo = document.getElementById('metodoPago');
    var buscador = document.getElementById('buscador');
    var sinResultados = document.getElementById('sinResultados');
    var form = document.getElementById('formVenta');

    var tipoActual = 'BEBIDA';

    function dinero(n) {
        return '$' + n.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function redondear(n) { return Math.round(n * 100) / 100; }

    function total() {
        var t = 0;
        Object.keys(pedido).forEach(function (id) { t += pedido[id].precio * pedido[id].cantidad; });
        return redondear(t);
    }

    // ---------- Menú: pestañas y búsqueda ----------
    function filtrar() {
        var texto = buscador.value.trim().toLowerCase();
        var visibles = 0;
        tarjetas.forEach(function (t) {
            var coincide = t.getAttribute('data-tipo') === tipoActual &&
                t.getAttribute('data-nombre').toLowerCase().indexOf(texto) !== -1;
            t.classList.toggle('oculto', !coincide);
            if (coincide) visibles++;
        });
        sinResultados.classList.toggle('oculto', visibles > 0);
    }

    document.querySelectorAll('.pestana').forEach(function (p) {
        p.addEventListener('click', function () {
            tipoActual = p.getAttribute('data-tipo');
            document.querySelectorAll('.pestana').forEach(function (o) { o.classList.toggle('activa', o === p); });
            filtrar();
        });
    });
    buscador.addEventListener('input', filtrar);

    // ---------- Pedido ----------
    function agregar(id, nombre, precio) {
        if (!pedido[id]) pedido[id] = { nombre: nombre, precio: precio, cantidad: 0 };
        if (pedido[id].cantidad < 99) pedido[id].cantidad++;
        dibujar();
    }

    function quitar(id) {
        if (!pedido[id]) return;
        pedido[id].cantidad--;
        if (pedido[id].cantidad <= 0) delete pedido[id];
        dibujar();
    }

    tarjetas.forEach(function (t) {
        t.querySelector('.btn-mas').addEventListener('click', function () {
            agregar(t.getAttribute('data-id'), t.getAttribute('data-nombre'), parseFloat(t.getAttribute('data-precio')));
        });
    });

    function dibujar() {
        // Se limpian solo las filas de productos, no el mensaje de "pedido vacío"
        lista.querySelectorAll('.pedido-item').forEach(function (f) { f.remove(); });
        var ids = Object.keys(pedido);
        vacio.classList.toggle('oculto', ids.length > 0);

        ids.forEach(function (id) {
            var p = pedido[id];
            var fila = document.createElement('div');
            fila.className = 'pedido-item';

            var texto = document.createElement('span');
            texto.textContent = p.cantidad + ' x ' + p.nombre + ' - ' + dinero(p.precio * p.cantidad);

            var controles = document.createElement('span');
            controles.className = 'controles';
            var menos = document.createElement('button');
            menos.type = 'button'; menos.className = 'btn-cant'; menos.textContent = '−';
            menos.setAttribute('aria-label', 'Quitar uno de ' + p.nombre);
            menos.addEventListener('click', function () { quitar(id); });
            var mas = document.createElement('button');
            mas.type = 'button'; mas.className = 'btn-cant mas'; mas.textContent = '+';
            mas.setAttribute('aria-label', 'Agregar uno de ' + p.nombre);
            mas.addEventListener('click', function () { agregar(id, p.nombre, p.precio); });
            controles.appendChild(menos);
            controles.appendChild(mas);

            fila.appendChild(texto);
            fila.appendChild(controles);
            lista.appendChild(fila);
        });

        tarjetas.forEach(function (t) {
            t.querySelector('.btn-mas').classList.toggle('en-pedido', !!pedido[t.getAttribute('data-id')]);
        });

        var t = total();
        subtotalEl.textContent = dinero(t);
        totalEl.textContent = dinero(t);
        actualizarPago();
    }

    // ---------- Pago ----------
    function actualizarPago() {
        var t = total();
        var hayPedido = t > 0;
        var recibido = parseFloat(monto.value);
        var listo = false;

        if (metodo === 'EFECTIVO') {
            var valido = !isNaN(recibido) && recibido >= t;
            cambioEl.textContent = (hayPedido && valido) ? dinero(redondear(recibido - t)) : '$0.00';
            listo = hayPedido && valido;
        } else {
            listo = hayPedido;
        }
        btnRegistrar.disabled = !listo;
        btnRegistrar.classList.toggle('btn-fuerte', listo);
    }

    document.querySelectorAll('.pago-tab').forEach(function (tab) {
        tab.addEventListener('click', function () {
            metodo = tab.getAttribute('data-metodo');
            document.querySelectorAll('.pago-tab').forEach(function (o) { o.classList.toggle('activa', o === tab); });
            document.getElementById('pagoEfectivo').classList.toggle('oculto', metodo !== 'EFECTIVO');
            document.getElementById('pagoTarjeta').classList.toggle('oculto', metodo !== 'TARJETA');
            actualizarPago();
        });
    });
    monto.addEventListener('input', actualizarPago);

    // ---------- Enviar ----------
    form.addEventListener('submit', function (e) {
        if (btnRegistrar.disabled) { e.preventDefault(); return; }
        inputItems.value = Object.keys(pedido).map(function (id) { return id + ':' + pedido[id].cantidad; }).join(',');
        inputMetodo.value = metodo;
        monto.disabled = (metodo !== 'EFECTIVO'); // en tarjeta no se manda el monto
    });

    // El Enter dentro del monto no debe enviar la venta por accidente
    monto.addEventListener('keydown', function (e) { if (e.key === 'Enter') e.preventDefault(); });

    filtrar();
    dibujar();
})();
