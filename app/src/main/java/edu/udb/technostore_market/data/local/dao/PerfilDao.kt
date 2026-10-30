package edu.udb.technostore_market.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.udb.technostore_market.data.local.entity.PerfilEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDao {

    @Query("SELECT * FROM perfiles WHERE userId = :userId")
    fun observar(userId: String): Flow<PerfilEntity?>

    @Upsert
    suspend fun guardar(perfil: PerfilEntity)
}
