package edu.udb.technostore_market.model

enum class EstadoPedido(val etiqueta: String) {
    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado")
}

enum class MetodoPago(val etiqueta: String) {
    EFECTIVO("Efectivo contra entrega"),
    TARJETA("Tarjeta al recibir"),
    TRANSFERENCIA("Transferencia bancaria")
}

data class PedidoItem(
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String
) {
    val subtotal: Double get() = precio * cantidad
}

/** Pedido registrado por el usuario (historial de compras). */
data class Pedido(
    val id: Long,
    val fecha: Long,
    val total: Double,
    val direccion: String,
    val metodoPago: String,
    val estado: EstadoPedido,
    /** true si el pedido también fue enviado (POST) a la API REST. */
    val sincronizado: Boolean,
    val items: List<PedidoItem>
) {
    val totalArticulos: Int get() = items.sumOf { it.cantidad }
}
