package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import edu.udb.technostore_market.di.AppContainer

/** Crea los ViewModels inyectándoles sus repositorios (y los argumentos de navegación). */
class AppViewModelFactory(private val contenedor: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val viewModel: ViewModel = when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(contenedor.authRepository)
            CatalogoViewModel::class.java -> CatalogoViewModel(contenedor.productoRepository)
            DetalleProductoViewModel::class.java ->
                DetalleProductoViewModel(extras.createSavedStateHandle(), contenedor.productoRepository)
            CarritoViewModel::class.java -> CarritoViewModel(
                contenedor.carritoRepository,
                contenedor.pedidoRepository,
                contenedor.perfilRepository
            )
            PedidosViewModel::class.java -> PedidosViewModel(contenedor.pedidoRepository)
            DetallePedidoViewModel::class.java ->
                DetallePedidoViewModel(extras.createSavedStateHandle(), contenedor.pedidoRepository)
            PerfilViewModel::class.java -> PerfilViewModel(contenedor.perfilRepository)
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return viewModel as T
    }
}
