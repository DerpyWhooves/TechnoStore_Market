package edu.udb.technostore_market.data.repository

import edu.udb.technostore_market.data.local.dao.CarritoDao
import edu.udb.technostore_market.data.local.entity.CarritoEntity
import edu.udb.technostore_market.data.local.entity.toDomain
import edu.udb.technostore_market.model.ItemCarrito
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.util.llamadaSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** CRUD del carrito de compras sobre Room (separado por usuario). */
class CarritoRepository(
    private val dao: CarritoDao,
    private val sesion: SessionManager
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observarCarrito(): Flow<List<ItemCarrito>> =
        sesion.userIdFlow.flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else dao.observar(uid).map { lista -> lista.map { it.toDomain() } }
        }

    suspend fun agregar(producto: Producto, cantidad: Int = 1): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) {
            val uid = sesion.requerirUserId()
            if (producto.agotado) throw IllegalStateException("${producto.nombre} está agotado")

            val cantidadActual = dao.obtener(uid, producto.id)?.cantidad ?: 0
            val nuevaCantidad = cantidadActual + cantidad
            if (nuevaCantidad > producto.stock) {
                throw IllegalStateException("Solo hay ${producto.stock} unidades disponibles")
            }
            dao.guardar(
                CarritoEntity(
                    userId = uid,
                    productoId = producto.id,
                    nombre = producto.nombre,
                    precio = producto.precio,
                    imagenUrl = producto.imagenUrl,
                    cantidad = nuevaCantidad,
                    stock = producto.stock
                )
            )
        }
    }

    suspend fun actualizarCantidad(productoId: Int, cantidad: Int): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) {
            val uid = sesion.requerirUserId()
            val actual = dao.obtener(uid, productoId) ?: return@withContext
            when {
                cantidad <= 0 -> dao.eliminar(uid, productoId)
                cantidad > actual.stock -> throw IllegalStateException("Solo hay ${actual.stock} unidades disponibles")
                else -> dao.guardar(actual.copy(cantidad = cantidad))
            }
        }
    }

    suspend fun eliminar(productoId: Int): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) { dao.eliminar(sesion.requerirUserId(), productoId) }
    }

    suspend fun vaciar(): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) { dao.vaciar(sesion.requerirUserId()) }
    }
}
