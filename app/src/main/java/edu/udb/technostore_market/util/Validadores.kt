package edu.udb.technostore_market.util

/**
 * Validaciones de formularios. Cada función devuelve el mensaje de error
 * o null cuando el valor es válido (funciones puras, fáciles de probar).
 */
object Validadores {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val TELEFONO_REGEX = Regex("^[0-9]{8}$")

    const val PASSWORD_MIN = 6
    const val DIRECCION_MIN = 10

    fun validarNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es obligatorio"
        nombre.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
        else -> null
    }

    fun validarEmail(email: String): String? = when {
        email.isBlank() -> "El correo es obligatorio"
        !EMAIL_REGEX.matches(email.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    fun validarPasswordObligatoria(password: String): String? =
        if (password.isBlank()) "La contraseña es obligatoria" else null

    fun validarPassword(password: String): String? = when {
        password.isBlank() -> "La contraseña es obligatoria"
        password.length < PASSWORD_MIN -> "Debe tener al menos $PASSWORD_MIN caracteres"
        else -> null
    }

    fun validarConfirmacion(password: String, confirmacion: String): String? = when {
        confirmacion.isBlank() -> "Confirma tu contraseña"
        password != confirmacion -> "Las contraseñas no coinciden"
        else -> null
    }

    /** Teléfono de 8 dígitos (formato de El Salvador); se aceptan espacios y guiones. */
    fun validarTelefono(telefono: String): String? {
        val limpio = telefono.replace(" ", "").replace("-", "")
        return when {
            limpio.isBlank() -> "El teléfono es obligatorio"
            !TELEFONO_REGEX.matches(limpio) -> "Ingresa 8 dígitos (ej. 7123-4567)"
            else -> null
        }
    }

    fun validarDireccion(direccion: String): String? = when {
        direccion.isBlank() -> "La dirección de entrega es obligatoria"
        direccion.trim().length < DIRECCION_MIN ->
            "Describe mejor la dirección (mín. $DIRECCION_MIN caracteres)"
        else -> null
    }
}
