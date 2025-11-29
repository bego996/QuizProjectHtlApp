# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

QuizToGo is an Android quiz application built with Kotlin and Jetpack Compose. The app connects to a Spring Boot backend (running in Docker) to fetch quiz questions and display them to users.

**Package**: `com.app.quizapp`
**Min SDK**: 28 (Android 9.0)
**Target SDK**: 35
**Compile SDK**: 35
**Java Version**: 11

## Architecture

The app follows **MVVM (Model-View-ViewModel)** with **Clean Architecture** principles:

```
Presentation Layer (UI)
    └── Composables (QuizScreen.kt)
    └── ViewModels (QuizViewModel.kt)
    └── UI State (QuizUiState)
          ↓
Domain Layer (Business Logic)
    └── Models (QuizQuestion, Quiz)
    └── Repository Interface (QuizRepository)
    └── Result Wrapper (Result.kt)
          ↓
Data Layer (Data Sources)
    └── Repository Implementation (QuizRepositoryImpl)
    └── API Service (QuizApiService)
    └── DTOs (QuizDto)
          ↓
Backend (Spring Boot in Docker)
```

### Key Architectural Patterns:
- **MVVM**: Separation of UI logic from business logic
- **Repository Pattern**: Single source of truth for data
- **Dependency Injection**: Hilt for managing dependencies
- **StateFlow**: Reactive state management for UI
- **Coroutines**: Asynchronous operations
- **Result Wrapper**: Type-safe error handling

## Build and Development Commands

### Build
```bash
./gradlew build
```

### Run tests
```bash
# Run unit tests (JVM-based tests)
./gradlew test

# Run instrumented tests (requires Android device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests com.app.quizapp.ExampleUnitTest

# Run specific instrumented test
./gradlew connectedAndroidTest --tests com.app.quizapp.ExampleInstrumentedTest
```

### Install and run
```bash
# Install debug build on connected device/emulator
./gradlew installDebug

# Build and install release APK
./gradlew assembleRelease
```

### Clean build
```bash
./gradlew clean
```

### Lint and code quality
```bash
./gradlew lint
```

## Project Structure

```
app/src/main/java/com/app/quizapp/
├── di/                          # Dependency Injection (Hilt Modules)
│   ├── NetworkModule.kt         # Provides Retrofit, OkHttp
│   └── RepositoryModule.kt      # Binds Repository interface
├── data/
│   ├── remote/
│   │   ├── QuizApiService.kt    # Retrofit API interface
│   │   └── dto/                 # Data Transfer Objects
│   │       └── QuizDto.kt       # Backend JSON models
│   └── repository/
│       └── QuizRepositoryImpl.kt # Repository implementation
├── domain/
│   ├── model/                   # Domain models (app-wide)
│   │   └── QuizQuestion.kt
│   ├── repository/
│   │   └── QuizRepository.kt    # Repository interface
│   └── util/
│       └── Result.kt            # Error handling wrapper
├── presentation/
│   └── quiz/
│       ├── QuizViewModel.kt     # ViewModel with StateFlow
│       └── QuizScreen.kt        # Compose UI
├── ui/theme/                    # Material3 Theme
│   ├── Color.kt
│   ├── Type.kt
│   └── Theme.kt
├── QuizApplication.kt           # Application class (@HiltAndroidApp)
└── MainActivity.kt              # Entry point (@AndroidEntryPoint)
```

## Key Dependencies

### UI
- **Jetpack Compose BOM**: 2024.10.01
- **Material3**: Included in Compose BOM
- **Activity Compose**: 1.9.3
- **Lifecycle Runtime KTX**: 2.8.7

### Architecture
- **ViewModel Compose**: 2.8.7
- **Lifecycle Runtime Compose**: 2.8.7

### Dependency Injection
- **Hilt**: 2.51.1
- **Hilt Navigation Compose**: 1.2.0
- **KSP**: 2.0.21-1.0.28 (for annotation processing)

### Networking
- **Retrofit**: 2.11.0
- **Retrofit Gson Converter**: 2.11.0
- **OkHttp**: 4.12.0
- **OkHttp Logging Interceptor**: 4.12.0
- **Gson**: 2.10.1

### Coroutines
- **Kotlinx Coroutines Android**: 1.7.3

### Testing
- **JUnit**: 4.13.2 (unit testing)
- **AndroidX Test**: JUnit 1.1.5, Espresso 3.5.1 (instrumented testing)

## Backend Integration

### Backend Configuration
The app connects to a Spring Boot backend running in a Docker container.

**Important**: The backend URL must be configured in `di/NetworkModule.kt`:

```kotlin
// For Android Emulator
private const val BASE_URL = "http://10.0.2.2:8080/"

// For Physical Device (use your computer's local IP)
// private const val BASE_URL = "http://192.168.1.XXX:8080/"
```

**Note**: Android Emulator uses `10.0.2.2` as a special alias to the host machine's `localhost`.

### API Endpoints
Defined in `data/remote/QuizApiService.kt`:
- `GET /api/quiz` - Get all quizzes
- `GET /api/quiz/{id}` - Get quiz by ID
- `GET /api/questions/random` - Get random questions (with optional category/difficulty filters)
- `GET /api/categories` - Get all categories

**TODO**: Adjust these endpoints to match your Spring Boot backend.

### Data Flow
1. UI triggers action (e.g., load questions)
2. ViewModel calls Repository
3. Repository calls API Service (Retrofit)
4. Retrofit makes HTTP request to backend
5. Response is converted from JSON to DTO
6. DTO is mapped to Domain Model
7. Result (Success/Error) is returned to ViewModel
8. ViewModel updates UI State (StateFlow)
9. UI recomposes based on new state

## Important Files

### Dependency Injection
- **QuizApplication.kt**: Application class annotated with `@HiltAndroidApp`
- **di/NetworkModule.kt**: Provides Retrofit, OkHttp with logging interceptor
- **di/RepositoryModule.kt**: Binds repository interface to implementation

### Networking
- **data/remote/QuizApiService.kt**: Retrofit interface with API endpoints
- **data/remote/dto/QuizDto.kt**: DTOs matching backend JSON structure + mapping functions

### Business Logic
- **domain/repository/QuizRepository.kt**: Repository interface (abstraction)
- **data/repository/QuizRepositoryImpl.kt**: Repository implementation with error handling
- **domain/util/Result.kt**: Sealed class for Success/Error handling

### UI
- **presentation/quiz/QuizViewModel.kt**: Manages UI state, handles business logic
- **presentation/quiz/QuizScreen.kt**: Compose UI with Loading/Error/Success states
- **MainActivity.kt**: Entry point, sets up Compose and theme

## Common Development Tasks

### Adding a new API endpoint:
1. Add endpoint to `QuizApiService.kt`
2. Create DTO if needed in `dto/` folder
3. Add repository function in `QuizRepository.kt` interface
4. Implement in `QuizRepositoryImpl.kt`
5. Call from ViewModel

### Adding a new screen:
1. Create new package in `presentation/`
2. Create ViewModel with `@HiltViewModel`
3. Create Composable screen
4. Use `hiltViewModel()` to get ViewModel in Composable

### Changing backend URL:
1. Edit `BASE_URL` in `di/NetworkModule.kt`
2. Rebuild the app

### Debugging network calls:
- OkHttp Logging Interceptor logs all requests/responses in Logcat
- Filter Logcat for "OkHttp" to see network traffic
