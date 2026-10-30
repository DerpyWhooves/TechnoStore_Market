package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.PedidoRepository
import edu.udb.technostore_market.model.Pedido
import edu.udb.technostore_market.ui.navigation.Rutas
import edu.udb.technostore_market.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetallePedidoUiState(
    val cargando: Boolean = true,
    val pedido: Pedido? = null,
    val mensaje: String? = null,
    val eliminado: Boolean = false
)

class DetallePedidoViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: PedidoRepository
) : ViewModel() {

    private val pedidoId: Long = checkNotNull(savedStateHandle[Rutas.ARG_PEDIDO_ID])

    private val _uiState = MutableStateFlow(DetallePedidoUiState())
    val uiState: StateFlow<DetallePedidoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.observarPedido(pedidoId).collect { pedido ->
                _uiState.update { it.copy(cargando = false, pedido = pedido) }
            }
        }
    }

    fun cancelar() {
        viewModelScope.launch {
            repositorio.cancelarPedido(pedidoId).fold(
                onSuccess = { _uiState.update { it.copy(mensaje = "Pedido cancelado") } },
                onFailure = { e -> _uiState.update { it.copy(mensaje = e.toUserMessage()) } }
            )
        }
    }

    fun eliminar() {
        viewModelScope.launch {
            repositorio.eliminarPedido(pedidoId).fold(
                onSuccess = { _uiState.update { it.copy(eliminado = true) } },
                onFailure = { e -> _uiState.update { it.copy(mensaje = e.toUserMessage()) } }
            )
        }
    }

    fun consumirMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
