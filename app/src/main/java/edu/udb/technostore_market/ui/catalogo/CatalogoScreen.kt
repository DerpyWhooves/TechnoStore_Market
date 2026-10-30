package edu.udb.technostore_market.ui.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.udb.technostore_market.model.Producto
import edu.udb.technostore_market.ui.components.EstadoCargando
import edu.udb.technostore_market.ui.components.EstadoError
import edu.udb.technostore_market.ui.components.EstadoVacio
import edu.udb.technostore_market.ui.components.ProductoCard
import edu.udb.technostore_market.util.comoCategoria
import edu.udb.technostore_market.viewmodel.CatalogoViewModel

@Composable
fun CatalogoScreen(
    viewModel: CatalogoViewModel,
    onProductoClick: (Int) -> Unit,
    onAgregarAlCarrito: (Producto) -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = estado.busqueda,
            onValueChange = viewModel::onBusquedaChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar productos...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
            trailingIcon = {
                if (estado.busqueda.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        if (estado.categorias.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(estado.categorias) { categoria ->
                    FilterChip(
                        selected = estado.categoriaSeleccionada == categoria,
                        onClick = { viewModel.onCategoriaSeleccionada(categoria) },
                        label = { Text(categoria.comoCategoria()) }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        when {
            estado.cargando -> EstadoCargando()
            estado.error != null -> EstadoError(estado.error.orEmpty(), onReintentar = viewModel::cargarProductos)
            estado.productos.isEmpty() -> EstadoVacio(
                icono = Icons.Filled.Search,
                titulo = "Sin resultados",
                mensaje = "No encontramos productos con ese criterio."
            )
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(estado.productos, key = { it.id }) { producto ->
                    ProductoCard(
                        producto = producto,
                        onCardClick = { onProductoClick(producto.id) },
                        onAgregarClick = { onAgregarAlCarrito(producto) }
                    )
                }
            }
        }
    }
}
