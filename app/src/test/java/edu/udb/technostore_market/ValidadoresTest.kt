package edu.udb.technostore_market

import edu.udb.technostore_market.util.Validadores
import edu.udb.technostore_market.util.comoCategoria
import edu.udb.technostore_market.util.comoPrecio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadoresTest {

    @Test
    fun email_vacio_o_invalido_devuelve_error() {
        assertNotNull(Validadores.validarEmail(""))
        assertNotNull(Validadores.validarEmail("correo-sin-arroba"))
        assertNotNull(Validadores.validarEmail("a@b"))
    }

    @Test
    fun email_valido_no_devuelve_error() {
        assertNull(Validadores.validarEmail("cliente@technostore.com"))
        assertNull(Validadores.validarEmail("  cliente@technostore.com  "))
    }

    @Test
    fun password_requiere_minimo_de_caracteres() {
        assertNotNull(Validadores.validarPassword("12345"))
        assertNull(Validadores.validarPassword("123456"))
    }

    @Test
    fun confirmacion_debe_coincidir() {
        assertNotNull(Validadores.validarConfirmacion("abc123", "abc124"))
        assertNotNull(Validadores.validarConfirmacion("abc123", ""))
        assertNull(Validadores.validarConfirmacion("abc123", "abc123"))
    }

    @Test
    fun telefono_acepta_ocho_digitos_con_guion_o_espacio() {
        assertNull(Validadores.validarTelefono("71234567"))
        assertNull(Validadores.validarTelefono("7123-4567"))
        assertNotNull(Validadores.validarTelefono("1234"))
        assertNotNull(Validadores.validarTelefono("abcdefgh"))
        assertNotNull(Validadores.validarTelefono(""))
    }

    @Test
    fun direccion_requiere_descripcion_minima() {
        assertNotNull(Validadores.validarDireccion(""))
        assertNotNull(Validadores.validarDireccion("Casa 5"))
        assertNull(Validadores.validarDireccion("Col. Escalón, calle 5, casa 12"))
    }

    @Test
    fun nombre_requiere_tres_caracteres() {
        assertNotNull(Validadores.validarNombre(" "))
        assertNotNull(Validadores.validarNombre("Al"))
        assertNull(Validadores.validarNombre("Ana"))
    }

    @Test
    fun formatos_de_precio_y_categoria() {
        assertEquals("$15.50", 15.5.comoPrecio())
        assertEquals("$0.00", 0.0.comoPrecio())
        assertEquals("Mens shirts", "mens-shirts".comoCategoria())
    }
}
