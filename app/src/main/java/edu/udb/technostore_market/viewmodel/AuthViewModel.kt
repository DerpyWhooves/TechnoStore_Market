package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.AuthRepository
import edu.udb.technostore_market.util.Validadores
import edu.udb.technostore_market.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado inmutable de la pantalla de login / registro. */
data class AuthUiState(
    val cargando: Boolean = false,
    val autenticado: Boolean = false,
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmacionError: String? = null,
    /** Mensaje general (error o información) que la UI muestra en un Snackbar. */
    val mensaje: String? = null
)

class AuthViewModel(private val repositorio: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun haySesionActiva(): Boolean = repositorio.haySesionActiva()

    fun iniciarSesion(correo: String, password: String) {
        val emailError = Validadores.validarEmail(correo)
        val passwordError = Validadores.validarPasswordObligatoria(password)
        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }
        ejecutarAutenticacion { repositorio.iniciarSesion(correo, password) }
    }

    fun registrar(nombre: String, correo: String, password: String, confirmacion: String) {
        val nombreError = Validadores.validarNombre(nombre)
        val emailError = Validadores.validarEmail(correo)
        val passwordError = Validadores.validarPassword(password)
        val confirmacionError = Validadores.validarConfirmacion(password, confirmacion)
        if (listOf(nombreError, emailError, passwordError, confirmacionError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nombreError = nombreError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmacionError = confirmacionError
                )
            }
            return
        }
        ejecutarAutenticacion { repositorio.registrar(nombre, correo, password) }
    }

    fun iniciarSesionConGoogle(idToken: String) {
        ejecutarAutenticacion { repositorio.iniciarSesionConGoogle(idToken) }
    }

    fun recuperarPassword(correo: String) {
        val emailError = Validadores.validarEmail(correo)
        if (emailError != null) {
            _uiState.update { it.copy(emailError = emailError) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensaje = null) }
            repositorio.recuperarPassword(correo).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(cargando = false, mensaje = "Te enviamos un correo para restablecer tu contraseña")
                    }
                },
                onFailure = { e -> _uiState.update { it.copy(cargando = false, mensaje = e.toUserMessage()) } }
            )
        }
    }

    fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(cargando = false, mensaje = mensaje) }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        _uiState.value = AuthUiState()
    }

    /** Limpia errores al cambiar entre las vistas de login y registro. */
    fun reiniciarEstado() {
        _uiState.value = AuthUiState()
    }

    fun consumirMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }

    private fun ejecutarAutenticacion(accion: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    cargando = true,
                    mensaje = null,
                    nombreError = null,
                    emailError = null,
                    passwordError = null,
                    confirmacionError = null
                )
            }
            accion().fold(
                onSuccess = { _uiState.update { it.copy(cargando = false, autenticado = true) } },
                onFailure = { e -> _uiState.update { it.copy(cargando = false, mensaje = e.toUserMessage()) } }
            )
        }
    }
}
