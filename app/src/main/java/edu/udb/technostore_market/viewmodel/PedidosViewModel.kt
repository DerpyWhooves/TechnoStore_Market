package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.PedidoRepository
import edu.udb.technostore_market.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PedidosUiState(
    val cargando: Boolean = true,
    val pedidos: List<Pedido> = emptyList()
)

/** Historial de compras del usuario. */
class PedidosViewModel(repositorio: PedidoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PedidosUiState())
    val uiState: StateFlow<PedidosUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.observarPedidos().collect { lista ->
                _uiState.update { it.copy(cargando = false, pedidos = lista) }
            }
        }
    }
}
