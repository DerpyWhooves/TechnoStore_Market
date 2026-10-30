package edu.udb.technostore_market.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import edu.udb.technostore_market.R
import edu.udb.technostore_market.ui.components.CampoTexto
import edu.udb.technostore_market.ui.components.EfectoMensaje
import edu.udb.technostore_market.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
    onLoginExitoso: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var esLogin by rememberSaveable { mutableStateOf(true) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }

    // Navega cuando la autenticación fue exitosa
    LaunchedEffect(estado.autenticado) {
        if (estado.autenticado) onLoginExitoso()
    }
    EfectoMensaje(estado.mensaje, snackbarHostState, viewModel::consumirMensaje)

    // Google Sign-In
    val webClientId = stringResource(id = R.string.default_web_client_id)
    val googleSignInClient = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    )
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        try {
            val cuenta = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
                .getResult(ApiException::class.java)
            cuenta.idToken?.let(viewModel::iniciarSesionConGoogle)
                ?: viewModel.mostrarError("Google no devolvió un token válido")
        } catch (e: ApiException) {
            viewModel.mostrarError("No se pudo iniciar con Google (código ${e.statusCode})")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Storefront,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("TechnoStore Market", style = MaterialTheme.typography.headlineMedium)
        Text(
            if (esLogin) "Inicia sesión para continuar" else "Crea tu cuenta",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        if (!esLogin) {
            CampoTexto(
                valor = nombre,
                onValorChange = { nombre = it },
                etiqueta = "Nombre completo",
                icono = Icons.Filled.Person,
                error = estado.nombreError
            )
        }
        CampoTexto(
            valor = correo,
            onValorChange = { correo = it },
            etiqueta = "Correo electrónico",
            icono = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            error = estado.emailError
        )
        CampoTexto(
            valor = password,
            onValorChange = { password = it },
            etiqueta = "Contraseña",
            icono = Icons.Filled.Lock,
            esPassword = true,
            imeAction = if (esLogin) ImeAction.Done else ImeAction.Next,
            error = estado.passwordError
        )
        if (!esLogin) {
            CampoTexto(
                valor = confirmacion,
                onValorChange = { confirmacion = it },
                etiqueta = "Confirmar contraseña",
                icono = Icons.Filled.Lock,
                esPassword = true,
                imeAction = ImeAction.Done,
                error = estado.confirmacionError
            )
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (esLogin) viewModel.iniciarSesion(correo, password)
                else viewModel.registrar(nombre, correo, password, confirmacion)
            },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(if (esLogin) "Ingresar" else "Registrarme")
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                // Cerramos la sesión previa de Google para que siempre pida elegir cuenta
                googleSignInClient.signOut().addOnCompleteListener {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                }
            },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar con Google")
        }

        Spacer(Modifier.height(8.dp))
        TextButton(onClick = {
            esLogin = !esLogin
            viewModel.reiniciarEstado()
        }) {
            Text(if (esLogin) "¿No tienes cuenta? Regístrate" else "¿Ya tienes cuenta? Inicia sesión")
        }
        if (esLogin) {
            TextButton(onClick = { viewModel.recuperarPassword(correo) }, enabled = !estado.cargando) {
                Text("¿Olvidaste tu contraseña?")
            }
        }
    }
}
