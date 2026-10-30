package edu.udb.technostore_market.model

/** Línea del carrito de compras del usuario. */
data class ItemCarrito(
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val imagenUrl: String,
    val cantidad: Int,
    val stock: Int
) {
    val subtotal: Double get() = precio * cantidad
}
