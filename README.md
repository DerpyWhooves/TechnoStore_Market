# 🛒 TechnoStore Market

Aplicación móvil Android para administrar los pedidos de una tienda de conveniencia.
Proyecto de Cátedra — **Desarrollo de Software para Móviles** (Universidad Don Bosco, Facultad de Ingeniería, Escuela de Computación).

## 📋 Descripción

TechnoStore Market permite a un cliente:

| Requerimiento del caso de estudio | Implementación |
|---|---|
| Registrarse e iniciar sesión | Firebase Authentication (correo/contraseña + Google) con validaciones y recuperación de contraseña |
| Consultar el catálogo | `GET /products` de la API REST DummyJSON (Retrofit) con filtro por categoría |
| Buscar productos | `GET /products/search?q=` con *debounce* de 400 ms |
| Agregar productos al carrito | Carrito persistente en **Room**, separado por usuario, con control de stock |
| Registrar pedidos | Pedido guardado en Room + `POST /carts/add` al servidor |
| Consultar historial de compras | Lista de pedidos, detalle, cancelación y eliminación |
| Administrar su perfil | Nombre (Firebase) + teléfono y dirección (Room), con validaciones |

## 👥 Integrantes

| Nombre | Carné |
|--|-|
| Guillermo Alejandro Espinoza Barahona | EB170303 |
| Christopher Steven Jovel Beltrán | JB251834 |
| Elisasbet Beatriz Marroquín Gonzalez | MG251360 |
| Edgardo Giovanni Cortez Valladares  | CV211195|

## 🧰 Tecnologías

- **Lenguaje:** Kotlin 2.2 · **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVVM + Repository Pattern + UI State (`StateFlow`)
- **Navegación:** Navigation Compose (`NavHost`, `NavController`, argumentos tipados)
- **Persistencia:** Room (Entity, DAO, Database, Repository) + `@Relation` 1-N + transacciones
- **Autenticación:** Firebase Authentication + Google Sign-In
- **API REST:** Retrofit 2 + Gson + OkHttp (GET y POST) — [DummyJSON](https://dummyjson.com)
- **Asincronía:** Corrutinas (`suspend`, `viewModelScope.launch`, `Dispatchers.IO`) y `Flow`
- **Imágenes:** Coil

## 🏗️ Arquitectura

```
┌─────────────────────────────── UI (Jetpack Compose) ───────────────────────────────┐
│  Screens ──► observan UiState (StateFlow) ──► Componentes reutilizables (M3)       │
└───────────────▲──────────────────────────────────────────┬─────────────────────────┘
                │ StateFlow<UiState>                       │ eventos (onClick, etc.)
┌───────────────┴──────────────────────────────────────────▼─────────────────────────┐
│  ViewModel  (MutableStateFlow privado → StateFlow público, viewModelScope.launch)  │
└───────────────▲────────────────────────────────────────────────────────────────────┘
                │ Result<T> / Flow<T>
┌───────────────┴────────────────────────────────────────────────────────────────────┐
│  Repository  (withContext(Dispatchers.IO), manejo de excepciones → Result)         │
└───────▲───────────────────▲──────────────────────────▲─────────────────────────────┘
        │                   │                          │
   Room (DAO)        Retrofit (DummyJSON)       Firebase Auth
```

**Flujo de datos unidireccional:** la UI solo lee `UiState` y envía eventos al ViewModel; el ViewModel
nunca conoce a Compose; el Repository nunca conoce al ViewModel. Las dependencias se inyectan con un
contenedor manual (`AppContainer`) y una `AppViewModelFactory`.

### Estructura del proyecto

```
app/src/main/java/edu/udb/technostore_market/
├── MainActivity.kt                # Punto de entrada (Compose)
├── TechnoStoreApp.kt              # Application: crea el AppContainer
├── di/AppContainer.kt             # Inyección de dependencias manual
├── model/                         # Modelos de dominio (Producto, ItemCarrito, Pedido, PerfilUsuario)
├── data/
│   ├── local/                     # Room
│   │   ├── AppDatabase.kt
│   │   ├── entity/                # CarritoEntity, PedidoEntity(+items), PerfilEntity
│   │   └── dao/                   # CarritoDao, PedidoDao, PerfilDao
│   ├── remote/                    # Retrofit
│   │   ├── DummyJsonApi.kt        # GET productos/búsqueda/categorías · POST carts/add
│   │   ├── NetworkModule.kt
│   │   └── dto/
│   └── repository/                # Auth, Producto, Carrito, Pedido, Perfil, SessionManager
├── viewmodel/                     # Un ViewModel + UiState por pantalla
├── util/                          # Validadores, manejo de errores, formatos
└── ui/
    ├── TechnoStoreRoot.kt         # Scaffold + TopAppBar + BottomNavigation + Snackbar
    ├── navigation/                # Rutas y NavHost
    ├── components/                # CampoTexto, ProductoCard, ItemCarritoCard, Dialogos, Estados...
    ├── auth/ catalogo/ detalle/ carrito/ pedidos/ perfil/   # Pantallas
    └── theme/                     # Tema Material 3 (claro/oscuro)
```

## 📱 Pantallas y componentes obligatorios

| Requisito | Dónde se usa |
|---|---|
| `Scaffold` | `ui/TechnoStoreRoot.kt` |
| `TopAppBar` | `CenterAlignedTopAppBar` en `TechnoStoreRoot.kt` (título dinámico, botón atrás, cerrar sesión) |
| `BottomNavigation` | `NavigationBar` (M3) con insignia del carrito en `TechnoStoreRoot.kt` |
| `LazyColumn` | Carrito, historial, detalle de pedido (`LazyRow` para categorías y `LazyVerticalGrid` en el catálogo) |
| `Card` | `ProductoCard`, `ItemCarritoCard`, tarjetas de pedido y resumen del carrito |
| `Dialog` | `DialogoConfirmacion` (quitar, vaciar, cancelar, eliminar, salir) y `DialogoPedido` (dirección + método de pago) |
| `Snackbar` | `SnackbarHost` global + `EfectoMensaje` (mensajes de un solo uso desde el ViewModel) |
| Argumentos entre pantallas | `producto/{productoId}` (Int) y `pedido/{pedidoId}` (Long), leídos con `SavedStateHandle` |

## 🌐 API REST (Retrofit)

Base URL: `https://dummyjson.com/`

| Método | Endpoint | Uso |
|---|---|---|
| GET | `/products?limit=100` | Catálogo |
| GET | `/products/search?q=` | Búsqueda |
| GET | `/products/category/{categoria}` | Filtro por categoría |
| GET | `/products/category-list` | Lista de categorías |
| GET | `/products/{id}` | Detalle del producto |
| POST | `/carts/add` | Registro del pedido en el servidor |

Los errores de red/HTTP se capturan en el repositorio (`llamadaSegura`) y se traducen a mensajes
legibles (`toUserMessage`). Si el POST falla por falta de conexión, el pedido igual se guarda en Room y se
marca como «guardado solo en el dispositivo».

## ✅ Validaciones

- **Registro:** nombre obligatorio (≥ 3), correo con formato válido, contraseña ≥ 6, confirmación idéntica.
- **Login:** correo válido y contraseña obligatoria; errores de Firebase traducidos (credenciales incorrectas, cuenta inexistente, sin conexión…).
- **Perfil:** nombre, teléfono de 8 dígitos y dirección (≥ 10 caracteres) obligatorios, con mensaje bajo cada campo.
- **Pedido:** dirección obligatoria y carrito no vacío; el stock limita la cantidad de cada producto.
- Las reglas viven en `util/Validadores.kt` (funciones puras) y tienen pruebas unitarias.

## ⚙️ Instalación

### Requisitos
- Android Studio (versión reciente compatible con **AGP 9.x**) y JDK 17+ (el proyecto usa el JDK de Android Studio)
- Dispositivo o emulador con **Android 10 (API 29) o superior** y servicios de Google Play
- Conexión a Internet (API REST y Firebase)

### Pasos
1. Clona el repositorio:
   ```bash
   git clone https://github.com/Chris103105/TechnoStore_Market.git
   ```
2. Ábrelo con **Android Studio** y espera a que Gradle sincronice.
3. El archivo `app/google-services.json` ya está incluido (proyecto Firebase `technostoremarket-3ec5e`).
   Para usar **Google Sign-In** el SHA-1 de la llave de depuración del equipo debe estar registrado en Firebase;
   el repositorio incluye `app/debug.keystore` compartida para ese fin.
4. En la consola de Firebase, habilita los proveedores **Correo/contraseña** y **Google** en *Authentication → Sign-in method*.
5. Ejecuta la app (`Run ▶`) en un emulador o dispositivo.

### Ejecutar pruebas
```bash
./gradlew test
```

## 📦 Dependencias principales

| Librería | Uso |
|---|---|
| Compose BOM + Material 3 + Material Icons Extended | Interfaz |
| Navigation Compose 2.9.x | Navegación |
| Lifecycle ViewModel / Runtime Compose 2.11 | MVVM y `collectAsStateWithLifecycle` |
| Room 2.8 (+ KSP) | Persistencia local |
| Retrofit 2.11 + Gson + OkHttp Logging | API REST |
| Kotlinx Coroutines (+ Play Services) | Corrutinas y `await()` de Firebase |
| Firebase Auth (BoM) + Play Services Auth | Autenticación |
| Coil Compose | Carga de imágenes |

Las versiones están centralizadas en `gradle/libs.versions.toml`.

## 🖼️ Capturas de pantalla

> LAS CAPTURAS IRAN AQUI.

| Login | Catálogo | Detalle |
|---|---|---|
| ![Login](docs/capturas/01_login.png) | ![Catálogo](docs/capturas/02_catalogo.png) | ![Detalle](docs/capturas/03_detalle.png) |

| Carrito | Confirmar pedido | Historial |
|---|---|---|
| ![Carrito](docs/capturas/04_carrito.png) | ![Pedido](docs/capturas/05_dialogo_pedido.png) | ![Historial](docs/capturas/06_historial.png) |

| Detalle de pedido | Perfil |
|---|---|
| ![Detalle pedido](docs/capturas/07_detalle_pedido.png) | ![Perfil](docs/capturas/08_perfil.png) |

## 🧪 Calidad de código

- Separación en capas (UI / ViewModel / Repository / fuentes de datos) y un modelo de dominio independiente de la API y de Room.
- Componentes Compose reutilizables (`CampoTexto`, `ProductoCard`, `ItemCarritoCard`, `DialogoConfirmacion`, `EstadoCargando/Error/Vacio`, `SelectorCantidad`, `EfectoMensaje`).
- Nombres descriptivos, `Result<T>` para errores esperados y cancelación de corrutinas respetada.
- Pruebas unitarias de validaciones y formatos (`app/src/test`).
