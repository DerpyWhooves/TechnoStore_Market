package edu.udb.technostore_market.ui.carrito

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.udb.technostore_market.model.ItemCarrito
import edu.udb.technostore_market.ui.components.DialogoConfirmacion
import edu.udb.technostore_market.ui.components.EstadoVacio
import edu.udb.technostore_market.ui.components.ItemCarritoCard
import edu.udb.technostore_market.util.comoPrecio
import edu.udb.technostore_market.viewmodel.CarritoViewModel

@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    onPedidoCreado: (Long) -> Unit,
    onIrAlCatalogo: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    var itemAEliminar by remember { mutableStateOf<ItemCarrito?>(null) }
    var mostrarVaciar by remember { mutableStateOf(false) }
    var mostrarPedido by remember { mutableStateOf(false) }

    // Cuando el pedido se crea, se cierra el diálogo y se navega al detalle
    LaunchedEffect(estado.pedidoCreadoId) {
        estado.pedidoCreadoId?.let { id ->
            mostrarPedido = false
            viewModel.consumirPedidoCreado()
            onPedidoCreado(id)
        }
    }

    if (estado.items.isEmpty()) {
        EstadoVacio(
            icono = Icons.Filled.RemoveShoppingCart,
            titulo = "Tu carrito está vacío",
            mensaje = "Agrega productos desde el catálogo para comenzar.",
            textoAccion = "Ir al catálogo",
            onAccion = onIrAlCatalogo
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estado.items, key = { it.productoId }) { item ->
                    ItemCarritoCard(
                        item = item,
                        onCantidadChange = { viewModel.cambiarCantidad(item, it) },
                        onEliminar = { itemAEliminar = item }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total (${estado.cantidadTotal} artículos)", style = MaterialTheme.typography.titleMedium)
                        Text(
                            estado.total.comoPrecio(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { mostrarVaciar = true }, modifier = Modifier.weight(1f)) {
                            Text("Vaciar")
                        }
                        Button(onClick = { mostrarPedido = true }, modifier = Modifier.weight(2f)) {
                            Text("Realizar pedido")
                        }
                    }
                }
            }
        }
    }

    itemAEliminar?.let { item ->
        DialogoConfirmacion(
            titulo = "Quitar producto",
            mensaje = "¿Quieres quitar \"${item.nombre}\" de tu carrito?",
            textoConfirmar = "Quitar",
            onConfirmar = {
                viewModel.eliminar(item)
                itemAEliminar = null
            },
            onCancelar = { itemAEliminar = null }
        )
    }

    if (mostrarVaciar) {
        DialogoConfirmacion(
            titulo = "Vaciar carrito",
            mensaje = "Se eliminarán todos los productos del carrito.",
            textoConfirmar = "Vaciar",
            onConfirmar = {
                viewModel.vaciar()
                mostrarVaciar = false
            },
            onCancelar = { mostrarVaciar = false }
        )
    }

    if (mostrarPedido) {
        DialogoPedido(
            total = estado.total,
            direccionInicial = estado.direccionPerfil,
            procesando = estado.procesandoPedido,
            onConfirmar = viewModel::realizarPedido,
            onCancelar = { mostrarPedido = false }
        )
    }
}
