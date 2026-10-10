function prepararEliminar(btn) {
    var id = btn.getAttribute('data-id');
    var nombre = btn.getAttribute('data-nombre');
    document.getElementById('nombreEliminar').textContent = nombre;
    document.getElementById('formEliminar').action = window.location.pathname.replace(/\/[^\/]*$/, '') + '/eliminar/' + id;
}

function toggleCamposMoto() {
    var select = document.getElementById('tipoSelect');
    var campos = document.getElementById('camposMoto');
    if (!select || !campos) return;
    if (select.value === 'MOTO') {
        campos.style.display = '';
    } else {
        campos.style.display = 'none';
    }
}

var filaIndex = 0;

function agregarFila() {
    var tbody = document.getElementById('filasProductos');
    if (!tbody) return;

    var opciones = productos.map(function(p) {
        return '<option value="' + p.id + '" data-precio="' + p.precio + '" data-stock="' + p.stock + '">' + p.nombre + ' (Stock: ' + p.stock + ')</option>';
    }).join('');

    var fila = document.createElement('tr');
    fila.id = 'fila-' + filaIndex;
    fila.innerHTML =
        '<td>' +
        '<select name="productoIds" class="form-select form-select-sm bg-dark text-white border-secondary" required onchange="actualizarPrecio(this, ' + filaIndex + ')">' +
        '<option value="">Seleccionar...</option>' + opciones +
        '</select>' +
        '</td>' +
        '<td><input type="number" name="cantidades" min="1" value="1" class="form-control form-control-sm bg-dark text-white border-secondary" required oninput="recalcularFila(' + filaIndex + ')"/></td>' +
        '<td><span id="precio-' + filaIndex + '" class="text-white-50">0.00</span></td>' +
        '<td><span id="subtotal-' + filaIndex + '" class="fw-bold">0.00</span></td>' +
        '<td><button type="button" class="btn btn-sm btn-outline-danger" onclick="eliminarFila(' + filaIndex + ')"><i class="bi bi-x"></i></button></td>';

    tbody.appendChild(fila);
    filaIndex++;
}

function actualizarPrecio(select, idx) {
    var opt = select.options[select.selectedIndex];
    var precio = parseFloat(opt.getAttribute('data-precio') || 0);
    document.getElementById('precio-' + idx).textContent = precio.toFixed(2);
    recalcularFila(idx);
}

function recalcularFila(idx) {
    var fila = document.getElementById('fila-' + idx);
    if (!fila) return;
    var select = fila.querySelector('select[name="productoIds"]');
    var cantInput = fila.querySelector('input[name="cantidades"]');
    var opt = select.options[select.selectedIndex];
    var precio = parseFloat(opt.getAttribute('data-precio') || 0);
    var cant = parseInt(cantInput.value) || 0;
    document.getElementById('subtotal-' + idx).textContent = (precio * cant).toFixed(2);
    calcularTotales();
}

function eliminarFila(idx) {
    var fila = document.getElementById('fila-' + idx);
    if (fila) fila.remove();
    calcularTotales();
}

function calcularTotales() {
    var subtotales = document.querySelectorAll('[id^="subtotal-"]');
    var suma = 0;
    subtotales.forEach(function(el) {
        suma += parseFloat(el.textContent) || 0;
    });

    var descuento = parseFloat(document.getElementById('descuento') ? document.getElementById('descuento').value : 0) || 0;
    var base = suma - descuento;
    if (base < 0) base = 0;

    var iva = typeof tasaIva !== 'undefined' ? tasaIva : 0.15;
    var ivaCalc = base * iva;
    var total = base + ivaCalc;

    var el = function(id) { return document.getElementById(id); };
    if (el('resSubtotal')) el('resSubtotal').textContent = suma.toFixed(2);
    if (el('resDescuento')) el('resDescuento').textContent = descuento.toFixed(2);
    if (el('resIva')) el('resIva').textContent = ivaCalc.toFixed(2);
    if (el('resTotal')) el('resTotal').textContent = total.toFixed(2);
}

document.addEventListener('DOMContentLoaded', function() {
    var alertas = document.querySelectorAll('.alert');
    alertas.forEach(function(a) {
        setTimeout(function() {
            var bs = bootstrap.Alert.getOrCreateInstance(a);
            if (bs) bs.close();
        }, 5000);
    });
});
