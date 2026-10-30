package edu.udb.technostore_market.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectorCantidad(
    cantidad: Int,
    minimo: Int,
    maximo: Int,
    onCambio: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FilledTonalIconButton(onClick = { onCambio(cantidad - 1) }, enabled = cantidad > minimo) {
            Icon(Icons.Filled.Remove, contentDescription = "Disminuir cantidad")
        }
        Text(
            text = cantidad.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        FilledTonalIconButton(onClick = { onCambio(cantidad + 1) }, enabled = cantidad < maximo) {
            Icon(Icons.Filled.Add, contentDescription = "Aumentar cantidad")
        }
    }
}
