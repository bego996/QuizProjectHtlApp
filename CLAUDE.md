# CLAUDE.md

## Project Overview
**QuizToGo** - Android quiz app with Kotlin + Jetpack Compose, connects to Spring Boot backend (Docker)

- **Package**: `com.app.quizapp`
- **SDK**: Min 28, Target/Compile 35, Java 11
- **Architecture**: MVVM + Clean Architecture (Presentation → Domain → Data)
- **DI**: Hilt, **State**: StateFlow, **Async**: Coroutines, **Network**: Retrofit + OkHttp + Gson
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
├── ui/theme/             # Material3 theme
├── QuizApplication.kt    # @HiltAndroidApp
└── MainActivity.kt       # @AndroidEntryPoint
```

## Key Files & Patterns
- **DI Setup**: `QuizApplication.kt`, `di/NetworkModule.kt` (Retrofit/OkHttp), `di/RepositoryModule.kt`
- **Data Flow**: UI → ViewModel → Repository (interface) → RepositoryImpl → ApiService → Backend
- **New API**: Add to `*ApiService.kt` → DTO in `dto/` → Interface in `domain/repository/` → Impl in `data/repository/` → Call from ViewModel
- **New Screen**: Package in `presentation/` → ViewModel with `@HiltViewModel` → Composable → Use `hiltViewModel()`

## Build Commands
- Build: `./gradlew build`
- Test: `./gradlew test` (unit), `./gradlew connectedAndroidTest` (instrumented)
- Install: `./gradlew installDebug`
- Debug Network: Logcat filter "OkHttp"

## Dependencies (Key Versions)
Compose BOM 2024.10.01, Hilt 2.51.1, Retrofit 2.11.0, OkHttp 4.12.0, Coroutines 1.7.3, Lifecycle 2.8.7

## Notes
- Uses `.claudeignore` to exclude files
- Network security: `network_security_config.xml` allows HTTP (for local backend)
