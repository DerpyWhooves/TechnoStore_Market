package edu.udb.technostore_market.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.ui.components.EstadoCargando
import edu.udb.technostore_market.ui.components.EstadoError
import edu.udb.technostore_market.ui.components.SelectorCantidad
import edu.udb.technostore_market.util.comoCategoria
import edu.udb.technostore_market.util.comoPrecio
import edu.udb.technostore_market.viewmodel.DetalleProductoViewModel

@Composable
fun DetalleProductoScreen(
    viewModel: DetalleProductoViewModel,
    onAgregarAlCarrito: (Producto, Int) -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val producto = estado.producto

    when {
        estado.cargando -> EstadoCargando()
        estado.error != null -> EstadoError(estado.error.orEmpty(), onReintentar = viewModel::cargarProducto)
        producto != null -> ContenidoDetalle(producto, onAgregarAlCarrito)
    }
}

@Composable
private fun ContenidoDetalle(producto: Producto, onAgregar: (Producto, Int) -> Unit) {
    var cantidad by rememberSaveable { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = producto.imagenUrl,
            contentDescription = "Imagen de ${producto.nombre}",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
        Text(producto.nombre, style = MaterialTheme.typography.headlineMedium)
        if (producto.marca.isNotBlank()) {
            Text(
                producto.marca,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(producto.categoria.comoCategoria()) })
            Icon(
                Icons.Filled.Star,
                contentDescription = "Calificación",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(String.format(java.util.Locale.US, "%.1f", producto.calificacion))
        }
        Text(
            producto.precio.comoPrecio(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            if (producto.agotado) "Producto agotado" else "Disponibles: ${producto.stock} unidades",
            style = MaterialTheme.typography.bodyMedium,
            color = if (producto.agotado) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(producto.descripcion, style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(8.dp))
        if (!producto.agotado) {
            SelectorCantidad(
                cantidad = cantidad,
                minimo = 1,
                maximo = producto.stock,
                onCambio = { cantidad = it }
            )
        }
        Button(
            onClick = { onAgregar(producto, cantidad) },
            enabled = !producto.agotado,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = if (producto.agotado) "Agotado" else "Agregar al carrito",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
