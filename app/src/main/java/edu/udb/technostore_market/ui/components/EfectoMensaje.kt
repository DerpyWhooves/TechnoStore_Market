package edu.udb.technostore_market.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Muestra [mensaje] en un Snackbar y avisa con [onConsumido] para que el
 * ViewModel lo limpie (el mensaje se muestra una sola vez).
 */
@Composable
fun EfectoMensaje(
    mensaje: String?,
    snackbarHostState: SnackbarHostState,
    onConsumido: () -> Unit
) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            onConsumido()
        }
    }
}
