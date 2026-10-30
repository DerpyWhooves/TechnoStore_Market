package edu.udb.technostore_market.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import edu.udb.technostore_market.model.EstadoPedido
import edu.udb.technostore_market.model.Pedido
import edu.udb.technostore_market.model.PedidoItem

@Entity(tableName = "pedidos", indices = [Index("userId")])
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val fecha: Long,
    val total: Double,
    val direccion: String,
    val metodoPago: String,
    val estado: String,
    /** Id devuelto por la API REST (null si el POST falló). */
    val idRemoto: Int?
)

@Entity(
    tableName = "pedido_items",
    foreignKeys = [
        ForeignKey(
            entity = PedidoEntity::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("pedidoId")]
)
data class PedidoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val productoId: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagenUrl: String
)

/** Relación 1 a N: un pedido con sus líneas. */
data class PedidoConItems(
    @Embedded val pedido: PedidoEntity,
    @Relation(parentColumn = "id", entityColumn = "pedidoId")
    val items: List<PedidoItemEntity>
)

fun PedidoConItems.toDomain() = Pedido(
    id = pedido.id,
    fecha = pedido.fecha,
    total = pedido.total,
    direccion = pedido.direccion,
    metodoPago = pedido.metodoPago,
    estado = runCatching { EstadoPedido.valueOf(pedido.estado) }.getOrDefault(EstadoPedido.CONFIRMADO),
    sincronizado = pedido.idRemoto != null,
    items = items.map {
        PedidoItem(
            productoId = it.productoId,
            nombre = it.nombre,
            precio = it.precio,
            cantidad = it.cantidad,
            imagenUrl = it.imagenUrl
        )
    }
)
