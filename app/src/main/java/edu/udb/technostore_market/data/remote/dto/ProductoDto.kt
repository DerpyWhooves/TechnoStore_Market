package edu.udb.technostore_market.data.remote.dto

import edu.udb.technostore_market.model.Producto

/** Respuesta de DummyJSON para listados: { products: [...], total, skip, limit } */
data class ProductosResponseDto(
    val products: List<ProductoDto>?,
    val total: Int?
)

data class ProductoDto(
    val id: Int,
    val title: String?,
    val description: String?,
    val category: String?,
    val price: Double?,
    val rating: Double?,
    val stock: Int?,
    val brand: String?,
    val thumbnail: String?
)

fun ProductoDto.toDomain() = Producto(
    id = id,
    nombre = title.orEmpty(),
    descripcion = description.orEmpty(),
    categoria = category.orEmpty(),
    precio = price ?: 0.0,
    stock = stock ?: 0,
    marca = brand.orEmpty(),
    imagenUrl = thumbnail.orEmpty(),
    calificacion = rating ?: 0.0
)
