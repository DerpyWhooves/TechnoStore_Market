# Guía de defensa — TechnoStore Market

## Demo sugerida (5-7 min)
1. **Registro** con datos inválidos → mostrar mensajes de error por campo. Registrar cuenta válida.
2. **Catálogo** (GET Retrofit): buscar «phone», filtrar por categoría, abrir un producto (argumento `productoId`).
3. **Agregar al carrito** desde catálogo y detalle → Snackbar + insignia en la barra inferior.
4. **Carrito** (Room): cambiar cantidades, quitar un producto (Dialog), intentar superar el stock.
5. **Realizar pedido**: Dialog con dirección vacía (error) → completar → POST `/carts/add` → detalle del pedido.
6. **Historial**: cancelar un pedido (UPDATE) y eliminar otro (DELETE).
7. **Perfil**: editar teléfono/dirección (validaciones), cerrar sesión, iniciar con otra cuenta y mostrar que el carrito/historial es independiente.
8. Apagar el Wi-Fi: mostrar el estado de error con botón **Reintentar**.

## Decisiones técnicas (preguntas probables)
- **¿Por qué Room y no Firestore?** Carrito, pedidos y perfil son datos del dispositivo que deben funcionar offline; Firebase se usa solo para identidad (Auth). Room da CRUD completo, relaciones 1-N y transacciones.
- **¿Cómo se separan los datos por usuario?** Todas las tablas llevan `userId` (uid de Firebase). `SessionManager.userIdFlow` + `flatMapLatest` re-suscribe los Flows al cambiar de sesión.
- **¿Por qué `StateFlow` y un `UiState` inmutable?** Estado único y observable por pantalla; la UI es una función del estado. Los mensajes de un solo uso (Snackbar) se «consumen» desde el ViewModel.
- **¿Dónde se usa `Dispatchers.IO`?** En los repositorios (`withContext(Dispatchers.IO)`), para que el ViewModel (`viewModelScope.launch`) nunca bloquee el hilo principal.
- **¿Qué pasa si falla el POST?** El pedido se guarda igual en Room (`idRemoto = null`) y se muestra como «guardado solo en el dispositivo».
- **¿Cómo llegan los argumentos al ViewModel?** `NavBackStackEntry` → `SavedStateHandle` (`createSavedStateHandle()` en la `AppViewModelFactory`).
- **¿Por qué inyección manual y no Hilt?** El alcance es pequeño; `AppContainer` + factory mantienen el desacople sin agregar procesamiento de anotaciones adicional.
- **Transacción de pedido:** `PedidoDao.registrarPedido` inserta cabecera, líneas y vacía el carrito en una sola transacción.

## Mapeo contra la lista de cotejo
| Criterio | Evidencia |
|---|---|
| Arquitectura MVVM (15%) | `viewmodel/`, `data/repository/`, `model/`, `di/` |
| Gestión del estado (10%) | `MutableStateFlow`/`StateFlow` en cada ViewModel, `collectAsStateWithLifecycle` |
| Persistencia (15%) | `data/local/` — CRUD de carrito, pedidos (con cancelar/eliminar) y perfil |
| API REST (15%) | `data/remote/` — GET ×5 y POST, corrutinas, `llamadaSegura` + `toUserMessage` |
| Navegación (10%) | `ui/navigation/` — NavHost, NavController, 2 rutas con argumentos |
| Interfaz (10%) | Compose + M3, `ui/components/`, tema claro/oscuro |
| Calidad de código (10%) | Capas, nombres descriptivos, sin duplicidad, `util/Validadores` con pruebas |
| Documentación (5%) | `README.md` (agregar capturas en `docs/capturas/`) |
| Presentación (10%) | Esta guía |
