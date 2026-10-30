package edu.udb.technostore_market.data.remote.dto

/** Cuerpo del POST /carts/add */
data class AgregarCarritoRequest(
    val userId: Int,
    val products: List<ProductoCantidadDto>
)

data class ProductoCantidadDto(
    val id: Int,
    val quantity: Int
)

/** Respuesta simulada de DummyJSON al crear un carrito. */
data class CarritoRemotoDto(
    val id: Int,
    val total: Double?
)
