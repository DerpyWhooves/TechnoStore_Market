package edu.udb.technostore_market.ui.carrito

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import edu.udb.technostore_market.model.MetodoPago
import edu.udb.technostore_market.ui.components.CampoTexto
import edu.udb.technostore_market.util.Validadores
import edu.udb.technostore_market.util.comoPrecio

/** Diálogo para confirmar el pedido: dirección (validada) y método de pago. */
@Composable
fun DialogoPedido(
    total: Double,
    direccionInicial: String,
    procesando: Boolean,
    onConfirmar: (direccion: String, metodoPago: MetodoPago) -> Unit,
    onCancelar: () -> Unit
) {
    var direccion by remember(direccionInicial) { mutableStateOf(direccionInicial) }
    var error by remember { mutableStateOf<String?>(null) }
    var metodoPago by remember { mutableStateOf(MetodoPago.EFECTIVO) }

    AlertDialog(
        onDismissRequest = { if (!procesando) onCancelar() },
        title = { Text("Confirmar pedido") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Total a pagar: ${total.comoPrecio()}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                CampoTexto(
                    valor = direccion,
                    onValorChange = {
                        direccion = it
                        error = null
                    },
                    etiqueta = "Dirección de entrega",
                    icono = Icons.Filled.LocationOn,
                    error = error,
                    singleLine = false,
                    minLines = 2,
                    enabled = !procesando
                )
                Spacer(Modifier.height(8.dp))
                Text("Método de pago", style = MaterialTheme.typography.labelLarge)
                Column(modifier = Modifier.selectableGroup()) {
                    MetodoPago.entries.forEach { metodo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = metodoPago == metodo,
                                    onClick = { metodoPago = metodo },
                                    role = Role.RadioButton,
                                    enabled = !procesando
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = metodoPago == metodo, onClick = null, enabled = !procesando)
                            Text(metodo.etiqueta, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !procesando,
                onClick = {
                    val problema = Validadores.validarDireccion(direccion)
                    if (problema != null) error = problema else onConfirmar(direccion, metodoPago)
                }
            ) {
                if (procesando) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("Confirmar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar, enabled = !procesando) { Text("Cancelar") } }
    )
}
