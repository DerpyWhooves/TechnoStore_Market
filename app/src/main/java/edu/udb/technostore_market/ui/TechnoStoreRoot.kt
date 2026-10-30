package edu.udb.technostore_market.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edu.udb.technostore_market.ui.components.DialogoConfirmacion
import edu.udb.technostore_market.ui.components.EfectoMensaje
import edu.udb.technostore_market.ui.navigation.Rutas
import edu.udb.technostore_market.ui.navigation.TechnoStoreNavHost
import edu.udb.technostore_market.ui.navigation.destinosInferiores
import edu.udb.technostore_market.ui.navigation.navigarAPestana
import edu.udb.technostore_market.viewmodel.AuthViewModel
import edu.udb.technostore_market.viewmodel.CarritoViewModel

/**
 * Raíz de la UI: Scaffold con TopAppBar, BottomNavigation (NavigationBar M3)
 * y SnackbarHost compartido por todas las pantallas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnoStoreRoot() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val authViewModel: AuthViewModel = appViewModel()
    val carritoViewModel: CarritoViewModel = appViewModel()
    val carritoEstado by carritoViewModel.uiState.collectAsStateWithLifecycle()

    // Si ya hay sesión de Firebase se salta el login
    val destinoInicial = remember {
        if (authViewModel.haySesionActiva()) Rutas.CATALOGO else Rutas.LOGIN
    }

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val esPestanaPrincipal = rutaActual != null && destinosInferiores.any { it.ruta == rutaActual }
    val mostrarBarraSuperior = rutaActual != null && rutaActual != Rutas.LOGIN

    var mostrarDialogoSalir by rememberSaveable { mutableStateOf(false) }

    // Mensajes del carrito (agregar, quitar, pedido...) visibles en cualquier pantalla
    EfectoMensaje(carritoEstado.mensaje, snackbarHostState, carritoViewModel::consumirMensaje)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (mostrarBarraSuperior) {
                CenterAlignedTopAppBar(
                    title = { Text(tituloPara(rutaActual)) },
                    navigationIcon = {
                        if (!esPestanaPrincipal) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    },
                    actions = {
                        if (rutaActual == Rutas.PERFIL) {
                            IconButton(onClick = { mostrarDialogoSalir = true }) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (esPestanaPrincipal) {
                NavigationBar {
                    destinosInferiores.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = { navController.navigarAPestana(destino.ruta) },
                            icon = {
                                if (destino.ruta == Rutas.CARRITO && carritoEstado.cantidadTotal > 0) {
                                    BadgedBox(badge = { Badge { Text(carritoEstado.cantidadTotal.toString()) } }) {
                                        Icon(destino.icono, contentDescription = destino.etiqueta)
                                    }
                                } else {
                                    Icon(destino.icono, contentDescription = destino.etiqueta)
                                }
                            },
                            label = { Text(destino.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        TechnoStoreNavHost(
            navController = navController,
            destinoInicial = destinoInicial,
            authViewModel = authViewModel,
            carritoViewModel = carritoViewModel,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(padding)
        )
    }

    if (mostrarDialogoSalir) {
        DialogoConfirmacion(
            titulo = "Cerrar sesión",
            mensaje = "¿Seguro que quieres salir de tu cuenta?",
            textoConfirmar = "Salir",
            onConfirmar = {
                mostrarDialogoSalir = false
                authViewModel.cerrarSesion()
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            onCancelar = { mostrarDialogoSalir = false }
        )
    }
}

private fun tituloPara(ruta: String?): String = when (ruta) {
    Rutas.CARRITO -> "Mi carrito"
    Rutas.PEDIDOS -> "Historial de compras"
    Rutas.PERFIL -> "Mi perfil"
    Rutas.DETALLE_PRODUCTO -> "Detalle del producto"
    Rutas.DETALLE_PEDIDO -> "Detalle del pedido"
    else -> "TechnoStore Market"
}
