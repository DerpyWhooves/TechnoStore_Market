package edu.udb.technostore_market.ui.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.udb.technostore_market.model.EstadoPedido
import edu.udb.technostore_market.model.Pedido
import edu.udb.technostore_market.model.PedidoItem
import edu.udb.technostore_market.ui.components.DialogoConfirmacion
import edu.udb.technostore_market.ui.components.EfectoMensaje
import edu.udb.technostore_market.ui.components.EstadoCargando
import edu.udb.technostore_market.ui.components.EstadoVacio
import edu.udb.technostore_market.util.comoPrecio
import edu.udb.technostore_market.util.formatearFecha
import edu.udb.technostore_market.viewmodel.DetallePedidoViewModel

@Composable
fun DetallePedidoScreen(
    viewModel: DetallePedidoViewModel,
    snackbarHostState: SnackbarHostState,
    onVolver: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmarCancelar by remember { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    EfectoMensaje(estado.mensaje, snackbarHostState, viewModel::consumirMensaje)
    LaunchedEffect(estado.eliminado) { if (estado.eliminado) onVolver() }

    val pedido = estado.pedido
    when {
        estado.cargando -> EstadoCargando()
        pedido == null -> EstadoVacio(
            icono = Icons.Filled.Receipt,
            titulo = "Pedido no encontrado",
            mensaje = "Es posible que haya sido eliminado."
        )
        else -> ContenidoPedido(
            pedido = pedido,
            onCancelar = { confirmarCancelar = true },
            onEliminar = { confirmarEliminar = true }
        )
    }

    if (confirmarCancelar) {
        DialogoConfirmacion(
            titulo = "Cancelar pedido",
            mensaje = "El pedido quedará marcado como cancelado. ¿Deseas continuar?",
            textoConfirmar = "Cancelar pedido",
            onConfirmar = {
                viewModel.cancelar()
                confirmarCancelar = false
            },
            onCancelar = { confirmarCancelar = false }
        )
    }
    if (confirmarEliminar) {
        DialogoConfirmacion(
            titulo = "Eliminar del historial",
            mensaje = "Se borrará este pedido de tu historial de compras.",
            textoConfirmar = "Eliminar",
            onConfirmar = {
                viewModel.eliminar()
                confirmarEliminar = false
            },
            onCancelar = { confirmarEliminar = false }
        )
    }
}

@Composable
private fun ContenidoPedido(pedido: Pedido, onCancelar: () -> Unit, onEliminar: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleLarge)
                        EtiquetaEstado(pedido.estado)
                    }
                    DatoPedido("Fecha", formatearFecha(pedido.fecha))
                    DatoPedido("Dirección", pedido.direccion)
                    DatoPedido("Pago", pedido.metodoPago)
                    DatoPedido(
                        "Servidor",
                        if (pedido.sincronizado) "Pedido enviado a la API" else "Guardado solo en el dispositivo"
                    )
                }
            }
        }
        item {
            Text("Productos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
        }
        items(pedido.items, key = { it.productoId }) { LineaPedido(it) }
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleLarge)
                Text(
                    pedido.total.comoPrecio(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(16.dp))
            if (pedido.estado == EstadoPedido.CONFIRMADO) {
                Button(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) { Text("Cancelar pedido") }
                Spacer(Modifier.height(8.dp))
            }
            OutlinedButton(onClick = onEliminar, modifier = Modifier.fillMaxWidth()) {
                Text("Eliminar del historial")
            }
        }
    }
}

@Composable
private fun DatoPedido(etiqueta: String, valor: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "$etiqueta:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LineaPedido(item: PedidoItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = item.imagenUrl,
                contentDescription = "Imagen de ${item.nombre}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombre, style = MaterialTheme.typography.titleSmall)
                Text(
                    "${item.cantidad} x ${item.precio.comoPrecio()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(item.subtotal.comoPrecio(), style = MaterialTheme.typography.titleSmall)
        }
    }
}
