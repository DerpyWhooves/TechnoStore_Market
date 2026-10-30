package edu.udb.technostore_market.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import edu.udb.technostore_market.data.local.entity.PedidoConItems
import edu.udb.technostore_market.data.local.entity.PedidoEntity
import edu.udb.technostore_market.data.local.entity.PedidoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PedidoDao {

    @Transaction
    @Query("SELECT * FROM pedidos WHERE userId = :userId ORDER BY fecha DESC")
    abstract fun observarPedidos(userId: String): Flow<List<PedidoConItems>>

    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id AND userId = :userId")
    abstract fun observarPedido(id: Long, userId: String): Flow<PedidoConItems?>

    @Insert
    abstract suspend fun insertarPedido(pedido: PedidoEntity): Long

    @Insert
    abstract suspend fun insertarItems(items: List<PedidoItemEntity>)

    @Query("DELETE FROM carrito WHERE userId = :userId")
    abstract suspend fun vaciarCarrito(userId: String)

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id AND userId = :userId")
    abstract suspend fun actualizarEstado(id: Long, userId: String, estado: String)

    @Query("DELETE FROM pedidos WHERE id = :id AND userId = :userId")
    abstract suspend fun eliminar(id: Long, userId: String)

    /** Guarda el pedido, sus líneas y vacía el carrito en una sola transacción. */
    @Transaction
    open suspend fun registrarPedido(pedido: PedidoEntity, items: List<PedidoItemEntity>): Long {
        val id = insertarPedido(pedido)
        insertarItems(items.map { it.copy(pedidoId = id) })
        vaciarCarrito(pedido.userId)
        return id
    }
}
