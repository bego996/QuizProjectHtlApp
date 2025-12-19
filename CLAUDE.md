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
├── di/                    # NetworkModule.kt, RepositoryModule.kt, SecurityModule.kt
├── data/
│   ├── remote/           # AuthApiService, MemberApiService, LlmApiService, *ApiService.kt (with CRUD)
│   │                     # dto/*RequestDto.kt, dto/*ResponseDto.kt (Login, Register, Quiz, etc.)
│   ├── repository/       # *RepositoryImpl.kt
│   └── security/         # SecureTokenManager.kt, AuthInterceptor.kt
├── domain/
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   ├── security/         # TokenManager.kt (interface)
│   └── util/Result.kt    # Sealed class for error handling
├── presentation/         # *ViewModel.kt, *Screen.kt (Composables)
│   ├── [feature]/        # User features: login, register, home, quiz, profile, categories, etc.
│   └── admin/            # Admin features (ROLE_admin required)
│       ├── [entity]/     # CRUD screens: user, topic, question, answer, difficulty, status, userrole, userquestion
│       └── *.kt          # Root admin screens: UsersScreen, ActiveQuizScreen, GenerateQuizzesScreen
├── navigation/           # ApplicationNavGraph.kt, NavigationDestination.kt
├── ui/theme/             # Material3 theme
├── QuizApplication.kt    # @HiltAndroidApp
└── MainActivity.kt       # @AndroidEntryPoint
```

## Key Files & Patterns
- **DI Setup**: `QuizApplication.kt`, `di/NetworkModule.kt` (Retrofit/OkHttp), `di/RepositoryModule.kt`
- **Data Flow**: UI → ViewModel → Repository (interface) → RepositoryImpl → ApiService → Backend
- **Backend Integration**: Full API coverage (Auth, User Profile, Quiz, Admin CRUD, AI/LLM)
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
Compose BOM 2024.10.01, Hilt 2.51.1, Retrofit 2.11.0, OkHttp 4.12.0, Coroutines 1.7.3, Lifecycle 2.8.7, hilt-navigation-compose 1.2.0, security-crypto 1.1.0-alpha06, Turbine 1.0.0 (Flow testing), Truth (assertions)

## Testing Infrastructure
- **Location**: `app/src/test/java/com/app/quizapp/`
- **Pattern**: MockWebServer for Repository tests, Fake implementations for ViewModel tests

### Repository Tests (Integration)
- **Tool**: MockWebServer (mocks HTTP responses)
- **Pattern**: Retrofit + ApiService + DTO mapping tested together

### ViewModel Tests (Unit)
- **Tools**: Turbine (Flow testing), UnconfinedTestDispatcher (coroutines), Truth (assertions)
- **Pattern**: Fake repositories provide test data, ViewModel logic tested in isolation

- **Total**: 234 unit tests (88 repository + 146 ViewModel)

### Test Configuration
- **build.gradle.kts**: `testOptions.unitTests.isReturnDefaultValues = true` (mocks Android framework classes like Log)

## Error Handling
- **Result Type**: All repository methods return `Result<T>` (sealed class: `Success<T>` or `Error`)
- **UI Display**: Error messages from `uiState.error` shown in UI (currently no centralized Snackbar/Dialog strategy)

## Authentication & Security
- **Token Storage**: `TokenManager` interface in `domain/security/`, implemented by `SecureTokenManager` in `data/security/`
- **Encryption**: EncryptedSharedPreferences with AES256-GCM backed by Android Keystore (security-crypto:1.1.0-alpha06)
- **Auto-Injection**: `AuthInterceptor` automatically adds `Authorization: Bearer <token>` header to all HTTP requests
- **DI Module**: `di/SecurityModule.kt` provides `TokenManager` singleton
- **Usage**: Inject `TokenManager` in ViewModels → `saveToken()` after login → `getToken()`/`hasToken()`/`clearToken()` as needed
- **No Manual Headers**: Repositories don't handle auth - AuthInterceptor works transparently on HTTP layer

## Coding Conventions
- **Comments**: Always add compact comments to new functions. Document purpose and parameters
- **Async/Sync**: Use `viewModelScope.launch` for repository calls. Keep UI thread smooth with proper coroutine handling
- **Composable Naming**: Screens end with `Screen` (e.g., `LoginScreen`), reusable components without suffix
- **Feature Structure**: Each feature in own package: `presentation/[feature]/` contains Screen, ViewModel, and Destination

## Notes
- Uses `.claudeignore` to exclude files
- Network security: `network_security_config.xml` allows HTTP (for local backend)
- JWT token management implemented with secure encrypted storage (TODOs in navigation for user/admin role handling)
- Full REST API integration: Auth (login/register), User profile management, Quiz operations, Admin CRUD for all entities, AI/LLM quiz generation
- Ask me before every big task you need to do, if i want to have the code you changed showed in this terminal, because sometimes it consumes unnecessary tokens.
- Use always an compact summary when i ask you to upgrade the Claude.md after a big feature we made. It should fit to the actually format