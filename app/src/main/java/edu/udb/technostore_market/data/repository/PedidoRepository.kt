package edu.udb.technostore_market.data.repository

import edu.udb.technostore_market.data.local.dao.PedidoDao
import edu.udb.technostore_market.data.local.entity.PedidoEntity
import edu.udb.technostore_market.data.local.entity.PedidoItemEntity
import edu.udb.technostore_market.data.local.entity.toDomain
import edu.udb.technostore_market.data.remote.DummyJsonApi
import edu.udb.technostore_market.data.remote.dto.AgregarCarritoRequest
import edu.udb.technostore_market.data.remote.dto.ProductoCantidadDto
import edu.udb.technostore_market.model.EstadoPedido
import edu.udb.technostore_market.model.ItemCarrito
import edu.udb.technostore_market.model.MetodoPago
import edu.udb.technostore_market.model.Pedido
import edu.udb.technostore_market.util.Validadores
import edu.udb.technostore_market.util.llamadaSegura
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Pedidos e historial de compras.
 * - Room: guarda el pedido (CRUD local).
 * - Retrofit: envía el pedido al servidor con POST /carts/add.
 */
class PedidoRepository(
    private val dao: PedidoDao,
    private val api: DummyJsonApi,
    private val sesion: SessionManager
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observarPedidos(): Flow<List<Pedido>> =
        sesion.userIdFlow.flatMapLatest { uid ->
            if (uid == null) flowOf(emptyList())
            else dao.observarPedidos(uid).map { lista -> lista.map { it.toDomain() } }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observarPedido(id: Long): Flow<Pedido?> =
        sesion.userIdFlow.flatMapLatest { uid ->
            if (uid == null) flowOf(null)
            else dao.observarPedido(id, uid).map { it?.toDomain() }
        }

    /** Devuelve el id local del pedido creado. */
    suspend fun registrarPedido(
        items: List<ItemCarrito>,
        direccion: String,
        metodoPago: MetodoPago
    ): Result<Long> = llamadaSegura {
        withContext(Dispatchers.IO) {
            val uid = sesion.requerirUserId()
            if (items.isEmpty()) throw IllegalStateException("Tu carrito está vacío")
            Validadores.validarDireccion(direccion)?.let { throw IllegalArgumentException(it) }

            val idRemoto = enviarAlServidor(items)

            val pedido = PedidoEntity(
                userId = uid,
                fecha = System.currentTimeMillis(),
                total = items.sumOf { it.subtotal },
                direccion = direccion.trim(),
                metodoPago = metodoPago.etiqueta,
                estado = EstadoPedido.CONFIRMADO.name,
                idRemoto = idRemoto
            )
            val lineas = items.map {
                PedidoItemEntity(
                    pedidoId = 0,
                    productoId = it.productoId,
                    nombre = it.nombre,
                    precio = it.precio,
                    cantidad = it.cantidad,
                    imagenUrl = it.imagenUrl
                )
            }
            dao.registrarPedido(pedido, lineas)
        }
    }

    /** POST al servidor. Si falla (sin red), el pedido igual queda guardado localmente. */
    private suspend fun enviarAlServidor(items: List<ItemCarrito>): Int? =
        try {
            val solicitud = AgregarCarritoRequest(
                userId = USUARIO_API,
                products = items.map { ProductoCantidadDto(id = it.productoId, quantity = it.cantidad) }
            )
            api.registrarCarrito(solicitud).id
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }

    suspend fun cancelarPedido(id: Long): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) {
            dao.actualizarEstado(id, sesion.requerirUserId(), EstadoPedido.CANCELADO.name)
        }
    }

    suspend fun eliminarPedido(id: Long): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) { dao.eliminar(id, sesion.requerirUserId()) }
    }

    private companion object {
        /** DummyJSON solo acepta ids numéricos de sus usuarios de prueba. */
        const val USUARIO_API = 1
    }
}
