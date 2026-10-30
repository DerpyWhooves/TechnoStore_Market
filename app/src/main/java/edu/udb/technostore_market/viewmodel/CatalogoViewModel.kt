package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.ProductoRepository
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.util.toUserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val cargando: Boolean = true,
    val productos: List<Producto> = emptyList(),
    val categorias: List<String> = emptyList(),
    val categoriaSeleccionada: String? = null,
    val busqueda: String = "",
    val error: String? = null
)

class CatalogoViewModel(private val repositorio: ProductoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    private var cargaJob: Job? = null

    init {
        cargarCategorias()
        cargarProductos()
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            delay(RETARDO_BUSQUEDA_MS) // evita una petición por cada letra escrita
            ejecutarCarga()
        }
    }

    /** Selecciona una categoría; tocarla otra vez quita el filtro. */
    fun onCategoriaSeleccionada(categoria: String?) {
        _uiState.update {
            it.copy(categoriaSeleccionada = if (it.categoriaSeleccionada == categoria) null else categoria)
        }
        cargarProductos()
    }

    fun cargarProductos() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch { ejecutarCarga() }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            repositorio.obtenerCategorias().onSuccess { lista ->
                _uiState.update { it.copy(categorias = lista) }
            }
        }
    }

    private suspend fun ejecutarCarga() {
        _uiState.update { it.copy(cargando = true, error = null) }
        val estado = _uiState.value
        val categoria = estado.categoriaSeleccionada

        val resultado = when {
            estado.busqueda.isNotBlank() ->
                repositorio.buscarProductos(estado.busqueda.trim()).map { lista ->
                    if (categoria == null) lista else lista.filter { it.categoria == categoria }
                }
            categoria != null -> repositorio.obtenerPorCategoria(categoria)
            else -> repositorio.obtenerProductos()
        }

        resultado.fold(
            onSuccess = { lista -> _uiState.update { it.copy(cargando = false, productos = lista) } },
            onFailure = { e ->
                _uiState.update { it.copy(cargando = false, productos = emptyList(), error = e.toUserMessage()) }
            }
        )
    }

    private companion object {
        const val RETARDO_BUSQUEDA_MS = 400L
    }
}
