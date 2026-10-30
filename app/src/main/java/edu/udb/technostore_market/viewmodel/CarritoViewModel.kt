package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.CarritoRepository
import edu.udb.technostore_market.data.repository.PedidoRepository
import edu.udb.technostore_market.data.repository.PerfilRepository
import edu.udb.technostore_market.model.ItemCarrito
import edu.udb.technostore_market.model.MetodoPago
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CarritoUiState(
    val items: List<ItemCarrito> = emptyList(),
    val total: Double = 0.0,
    val cantidadTotal: Int = 0,
    val direccionPerfil: String = "",
    val procesandoPedido: Boolean = false,
    val mensaje: String? = null,
    /** Id del pedido recién creado; la UI navega y luego lo consume. */
    val pedidoCreadoId: Long? = null
)

/** ViewModel compartido por catálogo, detalle y carrito (se crea a nivel de Activity). */
class CarritoViewModel(
    private val carritoRepo: CarritoRepository,
    private val pedidoRepo: PedidoRepository,
    perfilRepo: PerfilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            carritoRepo.observarCarrito().collect { items ->
                _uiState.update {
                    it.copy(
                        items = items,
                        total = items.sumOf { item -> item.subtotal },
                        cantidadTotal = items.sumOf { item -> item.cantidad }
                    )
                }
            }
        }
        viewModelScope.launch {
            perfilRepo.observarPerfil().collect { perfil ->
                _uiState.update { it.copy(direccionPerfil = perfil?.direccion.orEmpty()) }
            }
        }
    }

    fun agregar(producto: Producto, cantidad: Int = 1) {
        viewModelScope.launch {
            carritoRepo.agregar(producto, cantidad).fold(
                onSuccess = { mostrarMensaje("${producto.nombre} agregado al carrito") },
                onFailure = { mostrarMensaje(it.toUserMessage()) }
            )
        }
    }

    fun cambiarCantidad(item: ItemCarrito, nuevaCantidad: Int) {
        viewModelScope.launch {
            carritoRepo.actualizarCantidad(item.productoId, nuevaCantidad)
                .onFailure { mostrarMensaje(it.toUserMessage()) }
        }
    }

    fun eliminar(item: ItemCarrito) {
        viewModelScope.launch {
            carritoRepo.eliminar(item.productoId).fold(
                onSuccess = { mostrarMensaje("${item.nombre} eliminado del carrito") },
                onFailure = { mostrarMensaje(it.toUserMessage()) }
            )
        }
    }

    fun vaciar() {
        viewModelScope.launch {
            carritoRepo.vaciar().fold(
                onSuccess = { mostrarMensaje("Carrito vaciado") },
                onFailure = { mostrarMensaje(it.toUserMessage()) }
            )
        }
    }

    fun realizarPedido(direccion: String, metodoPago: MetodoPago) {
        val items = _uiState.value.items
        viewModelScope.launch {
            _uiState.update { it.copy(procesandoPedido = true) }
            pedidoRepo.registrarPedido(items, direccion, metodoPago).fold(
                onSuccess = { id ->
                    _uiState.update {
                        it.copy(
                            procesandoPedido = false,
                            pedidoCreadoId = id,
                            mensaje = "¡Pedido #$id registrado correctamente!"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(procesandoPedido = false, mensaje = e.toUserMessage()) }
                }
            )
        }
    }

    fun consumirMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun consumirPedidoCreado() {
        _uiState.update { it.copy(pedidoCreadoId = null) }
    }

    private fun mostrarMensaje(texto: String) {
        _uiState.update { it.copy(mensaje = texto) }
    }
}
