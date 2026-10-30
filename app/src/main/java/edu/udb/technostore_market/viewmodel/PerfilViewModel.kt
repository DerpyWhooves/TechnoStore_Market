package edu.udb.technostore_market.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udb.technostore_market.data.repository.PerfilRepository
import edu.udb.technostore_market.util.Validadores
import edu.udb.technostore_market.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val correo: String = "",
    val nombre: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val nombreError: String? = null,
    val telefonoError: String? = null,
    val direccionError: String? = null,
    val mensaje: String? = null
)

class PerfilViewModel(private val repositorio: PerfilRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        // Se carga una sola vez; luego el usuario edita el formulario libremente.
        viewModelScope.launch {
            val perfil = repositorio.observarPerfil().filterNotNull().first()
            _uiState.update {
                it.copy(
                    cargando = false,
                    correo = perfil.correo,
                    nombre = perfil.nombre,
                    telefono = perfil.telefono,
                    direccion = perfil.direccion
                )
            }
        }
    }

    fun onNombreChange(valor: String) = _uiState.update { it.copy(nombre = valor, nombreError = null) }

    fun onTelefonoChange(valor: String) = _uiState.update { it.copy(telefono = valor, telefonoError = null) }

    fun onDireccionChange(valor: String) = _uiState.update { it.copy(direccion = valor, direccionError = null) }

    fun guardar() {
        val estado = _uiState.value
        val nombreError = Validadores.validarNombre(estado.nombre)
        val telefonoError = Validadores.validarTelefono(estado.telefono)
        val direccionError = Validadores.validarDireccion(estado.direccion)
        if (nombreError != null || telefonoError != null || direccionError != null) {
            _uiState.update {
                it.copy(nombreError = nombreError, telefonoError = telefonoError, direccionError = direccionError)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true) }
            repositorio.guardarPerfil(estado.nombre, estado.telefono, estado.direccion).fold(
                onSuccess = { _uiState.update { it.copy(guardando = false, mensaje = "Perfil actualizado") } },
                onFailure = { e -> _uiState.update { it.copy(guardando = false, mensaje = e.toUserMessage()) } }
            )
        }
    }

    fun consumirMensaje() = _uiState.update { it.copy(mensaje = null) }
}
