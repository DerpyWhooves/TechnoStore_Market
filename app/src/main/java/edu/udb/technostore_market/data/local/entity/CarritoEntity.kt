package edu.udb.technostore_market.data.local.entity

import androidx.room.Entity
import edu.udb.technostore_market.model.ItemCarrito

/** Carrito persistente por usuario: clave compuesta (usuario, producto). */
@Entity(tableName = "carrito", primaryKeys = ["userId", "productoId"])
data class CarritoEntity(
    val userId: String,
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val imagenUrl: String,
    val cantidad: Int,
    val stock: Int
)

fun CarritoEntity.toDomain() = ItemCarrito(
    productoId = productoId,
    nombre = nombre,
    precio = precio,
    imagenUrl = imagenUrl,
    cantidad = cantidad,
    stock = stock
)
