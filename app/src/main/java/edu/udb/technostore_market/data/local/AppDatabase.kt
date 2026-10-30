package edu.udb.technostore_market.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.udb.technostore_market.data.local.dao.CarritoDao
import edu.udb.technostore_market.data.local.dao.PedidoDao
import edu.udb.technostore_market.data.local.dao.PerfilDao
import edu.udb.technostore_market.data.local.entity.CarritoEntity
import edu.udb.technostore_market.data.local.entity.PedidoEntity
import edu.udb.technostore_market.data.local.entity.PedidoItemEntity
import edu.udb.technostore_market.data.local.entity.PerfilEntity

@Database(
    entities = [CarritoEntity::class, PedidoEntity::class, PedidoItemEntity::class, PerfilEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carritoDao(): CarritoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun perfilDao(): PerfilDao

    companion object {
        private const val NOMBRE = "technostore.db"

        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NOMBRE)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
