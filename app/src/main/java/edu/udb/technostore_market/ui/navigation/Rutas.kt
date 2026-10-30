package edu.udb.technostore_market.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

object Rutas {
    const val LOGIN = "login"
    const val CATALOGO = "catalogo"
    const val CARRITO = "carrito"
    const val PEDIDOS = "pedidos"
    const val PERFIL = "perfil"

    const val ARG_PRODUCTO_ID = "productoId"
    const val ARG_PEDIDO_ID = "pedidoId"

    const val DETALLE_PRODUCTO = "producto/{$ARG_PRODUCTO_ID}"
    const val DETALLE_PEDIDO = "pedido/{$ARG_PEDIDO_ID}"

    fun detalleProducto(id: Int) = "producto/$id"
    fun detallePedido(id: Long) = "pedido/$id"
}

/** Pestaña de la barra de navegación inferior. */
data class DestinoInferior(val ruta: String, val etiqueta: String, val icono: ImageVector)

val destinosInferiores = listOf(
    DestinoInferior(Rutas.CATALOGO, "Catálogo", Icons.Filled.Storefront),
    DestinoInferior(Rutas.CARRITO, "Carrito", Icons.Filled.ShoppingCart),
    DestinoInferior(Rutas.PEDIDOS, "Historial", Icons.Filled.History),
    DestinoInferior(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)
