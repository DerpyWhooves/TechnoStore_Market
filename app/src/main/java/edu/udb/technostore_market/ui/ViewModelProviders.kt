package edu.udb.technostore_market.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.udb.technostore_market.TechnoStoreApp
import edu.udb.technostore_market.viewmodel.AppViewModelFactory

/**
 * Obtiene un ViewModel con su fábrica. Dentro de un destino de Navigation Compose
 * el ViewModel queda ligado al NavBackStackEntry (y recibe sus argumentos).
 */
@Composable
inline fun <reified VM : ViewModel> appViewModel(): VM {
    val aplicacion = LocalContext.current.applicationContext as TechnoStoreApp
    val fabrica = remember(aplicacion) { AppViewModelFactory(aplicacion.container) }
    return viewModel(factory = fabrica)
}
