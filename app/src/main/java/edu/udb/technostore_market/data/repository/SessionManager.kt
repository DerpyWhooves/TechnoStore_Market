package edu.udb.technostore_market.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Expone el usuario autenticado en Firebase. Los repositorios de Room usan este
 * id para separar el carrito, los pedidos y el perfil de cada cuenta.
 */
class SessionManager(private val auth: FirebaseAuth) {

    /** Emite el uid actual (o null) cada vez que cambia la sesión. */
    val userIdFlow: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser?.uid) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    val userIdActual: String? get() = auth.currentUser?.uid

    val correoActual: String get() = auth.currentUser?.email.orEmpty()

    val nombreActual: String get() = auth.currentUser?.displayName.orEmpty()

    /** Uid del usuario o error si no hay sesión (para operaciones de escritura). */
    fun requerirUserId(): String =
        userIdActual ?: throw IllegalStateException("Tu sesión expiró. Inicia sesión nuevamente.")
}
