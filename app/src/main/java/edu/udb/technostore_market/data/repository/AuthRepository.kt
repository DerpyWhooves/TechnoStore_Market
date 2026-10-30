package edu.udb.technostore_market.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import edu.udb.technostore_market.util.llamadaSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/** Autenticación con Firebase Authentication (correo/contraseña y Google). */
class AuthRepository(private val auth: FirebaseAuth) {

    fun haySesionActiva(): Boolean = auth.currentUser != null

    suspend fun iniciarSesion(correo: String, password: String): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(correo.trim(), password).await()
        }
        Unit
    }

    suspend fun registrar(nombre: String, correo: String, password: String): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) {
            val resultado = auth.createUserWithEmailAndPassword(correo.trim(), password).await()
            val cambios = UserProfileChangeRequest.Builder().setDisplayName(nombre.trim()).build()
            resultado.user?.updateProfile(cambios)?.await()
        }
        Unit
    }

    suspend fun recuperarPassword(correo: String): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) { auth.sendPasswordResetEmail(correo.trim()).await() }
        Unit
    }

    suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = llamadaSegura {
        withContext(Dispatchers.IO) {
            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credencial).await()
        }
        Unit
    }

    fun cerrarSesion() = auth.signOut()
}
