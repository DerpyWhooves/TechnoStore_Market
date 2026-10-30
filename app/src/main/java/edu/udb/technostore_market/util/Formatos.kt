package edu.udb.technostore_market.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.comoPrecio(): String = "$" + String.format(Locale.US, "%.2f", this)

fun formatearFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-SV")).format(Date(millis))

/** "mens-shirts" -> "Mens shirts" */
fun String.comoCategoria(): String =
    replace("-", " ").replaceFirstChar { it.uppercase() }
