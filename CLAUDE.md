# CLAUDE.md

## Project Overview
**QuizToGo** - Android quiz app with Kotlin + Jetpack Compose, connects to Spring Boot backend (Docker)

- **Package**: `com.app.quizapp`
- **SDK**: Min 28, Target/Compile 35, Java 17
- **Architecture**: MVVM + Clean Architecture (Presentation → Domain → Data)
- **DI**: Hilt, **State**: StateFlow, **Async**: Coroutines, **Network**: Retrofit + OkHttp + Gson
- **Navigation**: Navigation Compose with NavGraph
- **Error Handling**: `Result<T>` sealed class (Success/Error)

## Critical Configuration
**Backend URL** in `di/NetworkModule.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"  // Emulator (10.0.2.2 = host localhost)
// private const val BASE_URL = "http://192.168.1.XXX:8080/"  // Physical device
```

## Project Structure
```
app/src/main/java/com/app/quizapp/
├── di/                    # NetworkModule.kt, RepositoryModule.kt
├── data/
│   ├── remote/           # *ApiService.kt, dto/*Dto.kt
│   └── repository/       # *RepositoryImpl.kt
├── domain/
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   └── util/Result.kt    # Sealed class for error handling
├── presentation/         # *ViewModel.kt, *Screen.kt (Composables)
│   └── [feature]/        # Each feature: Screen.kt, ViewModel.kt, Destination object
├── navigation/           # ApplicationNavGraph.kt, NavigationDestination.kt
├── ui/theme/             # Material3 theme
├── QuizApplication.kt    # @HiltAndroidApp
└── MainActivity.kt       # @AndroidEntryPoint
```

## Key Files & Patterns
- **DI Setup**: `QuizApplication.kt`, `di/NetworkModule.kt` (Retrofit/OkHttp), `di/RepositoryModule.kt`
- **Data Flow**: UI → ViewModel → Repository (interface) → RepositoryImpl → ApiService → Backend
- **New API**: Add to `*ApiService.kt` → DTO in `dto/` → Interface in `domain/repository/` → Impl in `data/repository/` → Call from ViewModel
- **New Screen**: Package in `presentation/[feature]/` → Create `*Destination` object (implements `NavigationDestination`) → ViewModel with `@HiltViewModel` → Composable `*Screen.kt` → Use `hiltViewModel()` → Add route to `ApplicationNavGraph.kt`

## Navigation System
- **NavGraph**: `navigation/ApplicationNavGraph.kt` (function: `MettingNavHost`)
- **Routes**: Each screen has a `Destination` object implementing `NavigationDestination` (defines `route: String` and `titleRes: Int`)
- **Pattern**: Routes are simple strings (e.g., "cover", "home", "login"). For routes with parameters, use `"route/{param}"` format
- **Start Route**: `CoverDestination.route` (Splash screen)

## State Management
- **UI State Pattern**: Each ViewModel uses `*UiState` data class
- **StateFlow**: `private val _uiState = MutableStateFlow(*UiState())` + `val uiState: StateFlow<*UiState> = _uiState.asStateFlow()`
- **Updates**: Use `_uiState.update { it.copy(property = newValue) }` in ViewModels
- **String Resources**: Prefer `stringResource(R.string.*)` in Composables. Hardcoded strings are used where dynamic content is needed

## Build Commands
- Build: `./gradlew build`
- Test: `./gradlew test` (unit), `./gradlew connectedAndroidTest` (instrumented)
- Install: `./gradlew installDebug`
- Debug Network: Logcat filter "OkHttp"

## Dependencies (Key Versions)
Compose BOM 2024.10.01, Hilt 2.51.1, Retrofit 2.11.0, OkHttp 4.12.0, Coroutines 1.7.3, Lifecycle 2.8.7, hilt-navigation-compose 1.2.0

## Error Handling
- **Result Type**: All repository methods return `Result<T>` (sealed class: `Success<T>` or `Error`)

- **UI Display**: Error messages from `uiState.error` shown in UI (currently no centralized Snackbar/Dialog strategy)

## Coding Conventions
- **Comments**: Always add compact comments to new functions. Document purpose and parameters
- **Async/Sync**: Use `viewModelScope.launch` for repository calls. Keep UI thread smooth with proper coroutine handling
- **Composable Naming**: Screens end with `Screen` (e.g., `LoginScreen`), reusable components without suffix
- **Feature Structure**: Each feature in own package: `presentation/[feature]/` contains Screen, ViewModel, and Destination

## Notes
- Uses `.claudeignore` to exclude files
- Network security: `network_security_config.xml` allows HTTP (for local backend)
- No authentication implemented yet (TODOs in navigation for user/admin role handling)