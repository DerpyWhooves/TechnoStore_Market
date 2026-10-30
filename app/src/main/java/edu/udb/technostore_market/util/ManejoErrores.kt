package edu.udb.technostore_market.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

private const val MENSAJE_SIN_CONEXION =
    "Sin conexión a Internet. Verifica tu red e inténtalo de nuevo."

/**
 * Ejecuta [bloque] capturando excepciones y devolviéndolas como [Result].
 * Las cancelaciones de corrutinas se relanzan para no romper la cancelación estructurada.
 */
suspend inline fun <T> llamadaSegura(crossinline bloque: suspend () -> T): Result<T> =
    try {
        Result.success(bloque())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }

/** Traduce cualquier excepción a un mensaje entendible para el usuario. */
fun Throwable.toUserMessage(): String = when (this) {
    is FirebaseNetworkException, is UnknownHostException, is ConnectException -> MENSAJE_SIN_CONEXION
    is FirebaseAuthWeakPasswordException -> "La contraseña es demasiado débil"
    is FirebaseAuthInvalidUserException -> "No existe una cuenta con ese correo"
    is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
    is FirebaseAuthUserCollisionException -> "Ya existe una cuenta registrada con ese correo"
    is HttpException -> "El servidor respondió con un error (código ${code()})"
    is IOException -> MENSAJE_SIN_CONEXION
    is IllegalStateException, is IllegalArgumentException ->
        message ?: "Ocurrió un error inesperado"
    else -> localizedMessage ?: "Ocurrió un error inesperado"
}
