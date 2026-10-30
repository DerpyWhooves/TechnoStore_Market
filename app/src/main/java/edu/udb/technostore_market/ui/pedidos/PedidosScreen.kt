package edu.udb.technostore_market.ui.pedidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.udb.technostore_market.model.Pedido
import edu.udb.technostore_market.ui.components.EstadoCargando
import edu.udb.technostore_market.ui.components.EstadoVacio
import edu.udb.technostore_market.util.comoPrecio
import edu.udb.technostore_market.util.formatearFecha
import edu.udb.technostore_market.viewmodel.PedidosViewModel

@Composable
fun PedidosScreen(viewModel: PedidosViewModel, onPedidoClick: (Long) -> Unit) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        estado.cargando -> EstadoCargando()
        estado.pedidos.isEmpty() -> EstadoVacio(
            icono = Icons.Filled.Receipt,
            titulo = "Aún no tienes compras",
            mensaje = "Cuando registres un pedido aparecerá aquí."
        )
        else -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(estado.pedidos, key = { it.id }) { pedido ->
                PedidoCard(pedido = pedido, onClick = { onPedidoClick(pedido.id) })
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: Pedido, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleMedium)
                EtiquetaEstado(pedido.estado)
            }
            Text(
                formatearFecha(pedido.fecha),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${pedido.totalArticulos} artículos", style = MaterialTheme.typography.bodyMedium)
                Text(
                    pedido.total.comoPrecio(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
