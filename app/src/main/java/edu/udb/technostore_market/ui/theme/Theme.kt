package edu.udb.technostore_market.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Azul80,
    primaryContainer = AzulContenedor80,
    secondary = Teal80,
    secondaryContainer = TealContenedor80,
    tertiary = Ambar80,
    tertiaryContainer = AmbarContenedor80
)

private val LightColorScheme = lightColorScheme(
    primary = Azul40,
    primaryContainer = AzulContenedor40,
    secondary = Teal40,
    secondaryContainer = TealContenedor40,
    tertiary = Ambar40,
    tertiaryContainer = AmbarContenedor40
)

@Composable
fun TechnoStore_MarketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivado por defecto para mantener la identidad visual de la marca.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
