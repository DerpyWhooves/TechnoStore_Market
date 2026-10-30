package edu.udb.technostore_market.data.remote

import edu.udb.technostore_market.data.remote.dto.AgregarCarritoRequest
import edu.udb.technostore_market.data.remote.dto.CarritoRemotoDto
import edu.udb.technostore_market.data.remote.dto.ProductoDto
import edu.udb.technostore_market.data.remote.dto.ProductosResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Servicio REST público https://dummyjson.com */
interface DummyJsonApi {

    @GET("products")
    suspend fun obtenerProductos(
        @Query("limit") limite: Int = 100
    ): ProductosResponseDto

    @GET("products/search")
    suspend fun buscarProductos(@Query("q") consulta: String): ProductosResponseDto

    @GET("products/category/{categoria}")
    suspend fun obtenerPorCategoria(@Path("categoria") categoria: String): ProductosResponseDto

    @GET("products/category-list")
    suspend fun obtenerCategorias(): List<String>

    @GET("products/{id}")
    suspend fun obtenerProducto(@Path("id") id: Int): ProductoDto

    /** Registra el pedido en el servidor (DummyJSON simula la creación). */
    @POST("carts/add")
    suspend fun registrarCarrito(@Body solicitud: AgregarCarritoRequest): CarritoRemotoDto
}
