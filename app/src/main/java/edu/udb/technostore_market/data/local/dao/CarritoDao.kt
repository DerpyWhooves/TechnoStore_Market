package edu.udb.technostore_market.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.udb.technostore_market.data.local.entity.CarritoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito WHERE userId = :userId ORDER BY nombre")
    fun observar(userId: String): Flow<List<CarritoEntity>>

    @Query("SELECT * FROM carrito WHERE userId = :userId AND productoId = :productoId")
    suspend fun obtener(userId: String, productoId: Int): CarritoEntity?

    @Upsert
    suspend fun guardar(item: CarritoEntity)

    @Query("DELETE FROM carrito WHERE userId = :userId AND productoId = :productoId")
    suspend fun eliminar(userId: String, productoId: Int)

    @Query("DELETE FROM carrito WHERE userId = :userId")
    suspend fun vaciar(userId: String)
}
