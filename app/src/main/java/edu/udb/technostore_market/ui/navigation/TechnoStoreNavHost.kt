package edu.udb.technostore_market.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.udb.technostore_market.ui.appViewModel
import edu.udb.technostore_market.ui.auth.LoginScreen
import edu.udb.technostore_market.ui.carrito.CarritoScreen
import edu.udb.technostore_market.ui.catalogo.CatalogoScreen
import edu.udb.technostore_market.ui.detalle.DetalleProductoScreen
import edu.udb.technostore_market.ui.pedidos.DetallePedidoScreen
import edu.udb.technostore_market.ui.pedidos.PedidosScreen
import edu.udb.technostore_market.ui.perfil.PerfilScreen
import edu.udb.technostore_market.viewmodel.AuthViewModel
import edu.udb.technostore_market.viewmodel.CarritoViewModel
import edu.udb.technostore_market.viewmodel.CatalogoViewModel
import edu.udb.technostore_market.viewmodel.DetallePedidoViewModel
import edu.udb.technostore_market.viewmodel.DetalleProductoViewModel
import edu.udb.technostore_market.viewmodel.PedidosViewModel
import edu.udb.technostore_market.viewmodel.PerfilViewModel

/** Grafo de navegación de la app (NavHost + argumentos entre pantallas). */
@Composable
fun TechnoStoreNavHost(
    navController: NavHostController,
    destinoInicial: String,
    authViewModel: AuthViewModel,
    carritoViewModel: CarritoViewModel,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = destinoInicial, modifier = modifier) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                snackbarHostState = snackbarHostState,
                onLoginExitoso = {
                    // Se elimina el login del historial para que "atrás" no regrese a él
                    navController.navigate(Rutas.CATALOGO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.CATALOGO) {
            val viewModel: CatalogoViewModel = appViewModel()
            CatalogoScreen(
                viewModel = viewModel,
                onProductoClick = { id -> navController.navigate(Rutas.detalleProducto(id)) },
                onAgregarAlCarrito = { producto -> carritoViewModel.agregar(producto) }
            )
        }

        // Argumento: id del producto (Int)
        composable(
            route = Rutas.DETALLE_PRODUCTO,
            arguments = listOf(navArgument(Rutas.ARG_PRODUCTO_ID) { type = NavType.IntType })
        ) {
            val viewModel: DetalleProductoViewModel = appViewModel()
            DetalleProductoScreen(
                viewModel = viewModel,
                onAgregarAlCarrito = { producto, cantidad -> carritoViewModel.agregar(producto, cantidad) }
            )
        }

        composable(Rutas.CARRITO) {
            CarritoScreen(
                viewModel = carritoViewModel,
                onPedidoCreado = { id -> navController.navigate(Rutas.detallePedido(id)) },
                onIrAlCatalogo = { navController.navigarAPestana(Rutas.CATALOGO) }
            )
        }

        composable(Rutas.PEDIDOS) {
            val viewModel: PedidosViewModel = appViewModel()
            PedidosScreen(
                viewModel = viewModel,
                onPedidoClick = { id -> navController.navigate(Rutas.detallePedido(id)) }
            )
        }

        // Argumento: id del pedido (Long)
        composable(
            route = Rutas.DETALLE_PEDIDO,
            arguments = listOf(navArgument(Rutas.ARG_PEDIDO_ID) { type = NavType.LongType })
        ) {
            val viewModel: DetallePedidoViewModel = appViewModel()
            DetallePedidoScreen(
                viewModel = viewModel,
                snackbarHostState = snackbarHostState,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.PERFIL) {
            val viewModel: PerfilViewModel = appViewModel()
            PerfilScreen(viewModel = viewModel, snackbarHostState = snackbarHostState)
        }
    }
}

/** Navega a una pestaña inferior conservando el estado de las demás. */
fun NavHostController.navigarAPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(Rutas.CATALOGO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
