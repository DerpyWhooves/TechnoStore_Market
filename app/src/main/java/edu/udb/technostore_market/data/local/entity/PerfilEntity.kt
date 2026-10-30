package edu.udb.technostore_market.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfiles")
data class PerfilEntity(
    @PrimaryKey val userId: String,
    val nombre: String,
    val telefono: String,
    val direccion: String
)
