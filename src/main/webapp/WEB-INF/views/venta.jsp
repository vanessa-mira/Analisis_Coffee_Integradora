<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_US" scope="request"/>
<c:set var="menu" value="venta" scope="request"/>
<c:set var="scriptPagina" value="venta.js" scope="request"/>
<jsp:include page="layout/header.jsp"/>

<form class="pos" id="formVenta" method="post" action="${ctx}/venta" autocomplete="off">

    <section aria-label="Menú">
        <c:if test="${not empty error}">
            <div class="alerta" role="alert"><c:out value="${error}"/></div>
        </c:if>

        <div class="menu-cabecera-pos">
            <h2>Menú</h2>
            <div class="buscador">
                <input type="search" id="buscador" placeholder="Buscar productos" aria-label="Buscar productos">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></svg>
            </div>
        </div>

        <div class="pestanas" role="tablist">
            <button type="button" class="pestana activa" data-tipo="BEBIDA" role="tab">Bebidas</button>
            <button type="button" class="pestana" data-tipo="ALIMENTO" role="tab">Alimentos</button>
        </div>

        <div class="productos-grid">
            <c:forEach var="p" items="${productos}">
                <article class="producto" data-id="${p.idProducto}" data-nombre="<c:out value='${p.nombre}'/>" data-precio="${p.precio}" data-tipo="${p.tipo}">
                    <h3><c:out value="${p.nombre}"/></h3>
                    <div class="fila-precio">
                        <span class="precio">$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></span>
                        <button type="button" class="btn-mas" aria-label="Agregar al pedido">+</button>
                    </div>
                </article>
            </c:forEach>
            <p class="sin-resultados oculto" id="sinResultados">No hay productos que coincidan con tu búsqueda.</p>
        </div>
    </section>

    <aside class="pedido" aria-label="Pedido actual">
        <h2>Pedido actual</h2>

        <div class="pedido-lista" id="pedidoLista">
            <div class="pedido-vacio" id="pedidoVacio">
                <svg viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linejoin="round"><path d="M12 14h24l-2 28H14L12 14z"/><path d="M18 14c0-4 2.5-8 6-8s6 4 6 8"/><rect x="19" y="24" width="10" height="10" rx="2"/></svg>
                Aún no hay productos seleccionados
            </div>
        </div>

        <div class="totales">
            <div class="fila subtotal"><span>Subtotal:</span><span id="subtotal">$0.00</span></div>
            <div class="fila total"><span>Total:</span><span id="total">$0.00</span></div>
        </div>

        <div class="pago-titulo">Método de Pago</div>
        <div class="pago-caja">
            <div class="pago-tabs">
                <button type="button" class="pago-tab activa" data-metodo="EFECTIVO">Efectivo</button>
                <button type="button" class="pago-tab" data-metodo="TARJETA">Tarjeta Débito / Crédito</button>
            </div>
            <div class="pago-cuerpo" id="pagoEfectivo">
                <div class="fila">
                    <div class="col">
                        <label for="montoRecibido">Monto recibido</label>
                        <input type="number" id="montoRecibido" name="montoRecibido" min="0" step="0.01" inputmode="decimal">
                    </div>
                    <div class="col">Cambio<strong id="cambio">$0.00</strong></div>
                </div>
            </div>
            <div class="pago-cuerpo oculto" id="pagoTarjeta">
                <p class="pago-nota">Cobra en la terminal bancaria y confirma. El sistema solo registra que el pago fue con tarjeta.</p>
            </div>
        </div>

        <input type="hidden" name="items" id="items">
        <input type="hidden" name="metodoPago" id="metodoPago" value="EFECTIVO">
        <button type="submit" class="btn btn-registrar" id="btnRegistrar" disabled>Registrar Venta</button>
    </aside>
</form>

<jsp:include page="layout/footer.jsp"/>
