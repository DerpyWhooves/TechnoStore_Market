package edu.udb.technostore_market.model

/** Producto del catálogo (modelo de dominio, independiente de la API). */
data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val marca: String,
    val imagenUrl: String,
    val calificacion: Double
) {
    val agotado: Boolean get() = stock <= 0
}
