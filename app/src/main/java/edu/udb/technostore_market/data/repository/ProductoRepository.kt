package edu.udb.technostore_market.data.repository

import edu.udb.technostore_market.data.remote.DummyJsonApi
import edu.udb.technostore_market.data.remote.dto.toDomain
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.util.llamadaSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Catálogo de productos obtenido de la API REST (GET con Retrofit). */
class ProductoRepository(private val api: DummyJsonApi) {

    suspend fun obtenerProductos(): Result<List<Producto>> = llamadaSegura {
        withContext(Dispatchers.IO) {
            api.obtenerProductos().products.orEmpty().map { it.toDomain() }
        }
    }

    suspend fun buscarProductos(consulta: String): Result<List<Producto>> = llamadaSegura {
        withContext(Dispatchers.IO) {
            api.buscarProductos(consulta).products.orEmpty().map { it.toDomain() }
        }
    }

    suspend fun obtenerPorCategoria(categoria: String): Result<List<Producto>> = llamadaSegura {
        withContext(Dispatchers.IO) {
            api.obtenerPorCategoria(categoria).products.orEmpty().map { it.toDomain() }
        }
    }

    suspend fun obtenerCategorias(): Result<List<String>> = llamadaSegura {
        withContext(Dispatchers.IO) { api.obtenerCategorias() }
    }

    suspend fun obtenerProducto(id: Int): Result<Producto> = llamadaSegura {
        withContext(Dispatchers.IO) { api.obtenerProducto(id).toDomain() }
    }
}
