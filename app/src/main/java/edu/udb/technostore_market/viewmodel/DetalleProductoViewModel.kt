package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.ProductoRepository
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.ui.navigation.Rutas
import edu.udb.technostore_market.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleProductoUiState(
    val cargando: Boolean = true,
    val producto: Producto? = null,
    val error: String? = null
)

/** El id del producto llega como argumento de navegación (SavedStateHandle). */
class DetalleProductoViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: ProductoRepository
) : ViewModel() {

    private val productoId: Int = checkNotNull(savedStateHandle[Rutas.ARG_PRODUCTO_ID])

    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    init {
        cargarProducto()
    }

    fun cargarProducto() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            repositorio.obtenerProducto(productoId).fold(
                onSuccess = { p -> _uiState.update { it.copy(cargando = false, producto = p) } },
                onFailure = { e -> _uiState.update { it.copy(cargando = false, error = e.toUserMessage()) } }
            )
        }
    }
}
