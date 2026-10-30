package edu.udb.technostore_market.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import edu.udb.technostore_market.data.local.AppDatabase
import edu.udb.technostore_market.data.remote.NetworkModule
import edu.udb.technostore_market.data.repository.AuthRepository
import edu.udb.technostore_market.data.repository.CarritoRepository
import edu.udb.technostore_market.data.repository.PedidoRepository
import edu.udb.technostore_market.data.repository.PerfilRepository
import edu.udb.technostore_market.data.repository.ProductoRepository
import edu.udb.technostore_market.data.repository.SessionManager

/** Contenedor de dependencias (inyección manual) creado una sola vez en la Application. */
class AppContainer(context: Context) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val baseDeDatos = AppDatabase.crear(context)

    val sesion = SessionManager(firebaseAuth)
    val authRepository = AuthRepository(firebaseAuth)
    val productoRepository = ProductoRepository(NetworkModule.api)
    val carritoRepository = CarritoRepository(baseDeDatos.carritoDao(), sesion)
    val pedidoRepository = PedidoRepository(baseDeDatos.pedidoDao(), NetworkModule.api, sesion)
    val perfilRepository = PerfilRepository(baseDeDatos.perfilDao(), sesion, firebaseAuth)
}
