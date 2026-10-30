package edu.udb.technostore_market.ui.pedidos

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.udb.technostore_market.model.EstadoPedido

@Composable
fun EtiquetaEstado(estado: EstadoPedido, modifier: Modifier = Modifier) {
    val (fondo, texto) = when (estado) {
        EstadoPedido.CONFIRMADO ->
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EstadoPedido.CANCELADO ->
            MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    Surface(color = fondo, contentColor = texto, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Text(
            estado.etiqueta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
