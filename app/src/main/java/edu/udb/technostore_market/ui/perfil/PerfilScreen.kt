package edu.udb.technostore_market.ui.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.udb.technostore_market.ui.components.CampoTexto
import edu.udb.technostore_market.ui.components.EfectoMensaje
import edu.udb.technostore_market.ui.components.EstadoCargando
import edu.udb.technostore_market.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(viewModel: PerfilViewModel, snackbarHostState: SnackbarHostState) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    EfectoMensaje(estado.mensaje, snackbarHostState, viewModel::consumirMensaje)

    if (estado.cargando) {
        EstadoCargando()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            Icons.Filled.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(estado.correo, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(16.dp))

        // El correo no se edita: es la identidad de la cuenta en Firebase
        CampoTexto(
            valor = estado.correo,
            onValorChange = {},
            etiqueta = "Correo electrónico",
            icono = Icons.Filled.Email,
            enabled = false
        )
        CampoTexto(
            valor = estado.nombre,
            onValorChange = viewModel::onNombreChange,
            etiqueta = "Nombre completo",
            icono = Icons.Filled.Person,
            error = estado.nombreError
        )
        CampoTexto(
            valor = estado.telefono,
            onValorChange = viewModel::onTelefonoChange,
            etiqueta = "Teléfono",
            icono = Icons.Filled.Phone,
            keyboardType = KeyboardType.Phone,
            error = estado.telefonoError
        )
        CampoTexto(
            valor = estado.direccion,
            onValorChange = viewModel::onDireccionChange,
            etiqueta = "Dirección de entrega",
            icono = Icons.Filled.LocationOn,
            error = estado.direccionError,
            singleLine = false,
            minLines = 2,
            imeAction = ImeAction.Done
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = viewModel::guardar,
            enabled = !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (estado.guardando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("Guardar cambios", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
