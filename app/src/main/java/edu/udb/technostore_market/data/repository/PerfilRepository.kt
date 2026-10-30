package edu.udb.technostore_market.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import edu.udb.technostore_market.data.local.dao.PerfilDao
import edu.udb.technostore_market.data.local.entity.PerfilEntity
import edu.udb.technostore_market.model.PerfilUsuario
import edu.udb.technostore_market.util.Validadores
import edu.udb.technostore_market.util.llamadaSegura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Perfil del usuario: nombre y correo vienen de Firebase Auth;
 * teléfono y dirección de entrega se guardan en Room.
 */
class PerfilRepository(
    private val dao: PerfilDao,
    private val sesion: SessionManager,
    private val auth: FirebaseAuth
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observarPerfil(): Flow<PerfilUsuario?> =
        sesion.userIdFlow.flatMapLatest { uid ->
            if (uid == null) flowOf(null)
            else dao.observar(uid).map { entidad ->
                PerfilUsuario(
                    userId = uid,
                    nombre = entidad?.nombre?.takeIf { it.isNotBlank() } ?: sesion.nombreActual,
                    correo = sesion.correoActual,
                    telefono = entidad?.telefono.orEmpty(),
                    direccion = entidad?.direccion.orEmpty()
                )
            }
        }

    suspend fun guardarPerfil(nombre: String, telefono: String, direccion: String): Result<Unit> =
        llamadaSegura {
            withContext(Dispatchers.IO) {
                val uid = sesion.requerirUserId()
                Validadores.validarNombre(nombre)?.let { throw IllegalArgumentException(it) }
                Validadores.validarTelefono(telefono)?.let { throw IllegalArgumentException(it) }
                Validadores.validarDireccion(direccion)?.let { throw IllegalArgumentException(it) }

                dao.guardar(
                    PerfilEntity(
                        userId = uid,
                        nombre = nombre.trim(),
                        telefono = telefono.replace(" ", "").replace("-", ""),
                        direccion = direccion.trim()
                    )
                )
                val cambios = UserProfileChangeRequest.Builder().setDisplayName(nombre.trim()).build()
                auth.currentUser?.updateProfile(cambios)?.await()
            }
            Unit
        }
}
