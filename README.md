# QuizToGo 📱

> A modern Android quiz application with AI-powered content generation and comprehensive admin management

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.10.01-green.svg)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28-orange.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-blue.svg)](https://developer.android.com/about/versions)

## 📖 Overview

**QuizToGo** is a sophisticated, production-ready Android quiz application built with Kotlin and Jetpack Compose. It features a complete backend integration with Spring Boot, JWT authentication, role-based access control, and cutting-edge AI-powered quiz generation using local LLM (Ollama).

The app serves two user types:
- **Regular Users**: Take quizzes, track progress, manage profiles, and explore categorized content
- **Administrators**: Full CRUD operations for all entities, AI quiz generation, and user management

### 🎯 Key Highlights

- ✅ **Modern Tech Stack**: 100% Kotlin with Jetpack Compose, Hilt, Coroutines, and Material3
- ✅ **Clean Architecture**: MVVM with Repository Pattern (Presentation → Domain → Data)
- ✅ **Enterprise Security**: JWT authentication with AES256-GCM encrypted token storage
- ✅ **AI-Powered**: Ollama LLM integration for automated quiz generation
- ✅ **Comprehensive Admin Panel**: 8+ CRUD screens for complete content management
- ✅ **Production Ready**: Error handling, loading states, form validation, and state management
- ✅ **Fully Tested**: Unit tests, instrumented tests, and MockWebServer integration

---

## ✨ Features

### 👤 User Features

#### Authentication & Profile
- **Secure Login/Registration**: Email-based authentication with password validation
- **Auto-Login**: Automatic authentication on app launch using stored JWT tokens
- **Profile Management**: Edit personal information (name, birthdate, nickname, email)
- **Password Change**: Secure password update with current password verification

#### Quiz Experience
- **Interactive Quizzes**: Multiple-choice questions with instant feedback
- **Progress Tracking**: Real-time score tracking and quiz completion status
- **Quiz Results**: Comprehensive result display with score breakdown
- **Quiz History**: View past attempts and performance metrics
- **Category Browsing**: Explore quizzes by categories, topics, and subtopics
- **Difficulty Levels**: Choose between Easy, Medium, and Hard questions

#### Dashboard & Stats
- **Home Dashboard**: Daily quiz recommendations, popular categories, and statistics
- **Performance Metrics**: Track quiz performance, favorite categories, and achievements
- **User Statistics**: View quiz history, success rates, and progress over time

### 👨‍💼 Admin Features

#### AI Quiz Generation (🤖 Powered by Ollama)
- **LLM Integration**: Generate quizzes using local Large Language Models
- **Custom Prompts**: Flexible prompt engineering for topic-specific quizzes
- **Model Selection**: Choose from available Ollama models
- **Streaming Support**: Real-time generation feedback
- **Save to Database**: One-click saving of AI-generated quizzes

#### Entity Management (Full CRUD)
Complete administrative control over:
- **Users**: Manage user accounts, roles, and permissions
- **Questions**: Create, edit, and organize quiz questions
- **Topics**: Structure content with categories and topics
- **Answers**: Manage answer options and correct answer designation
- **Difficulty Levels**: Define and assign difficulty ratings
- **Status Values**: Control content workflow (Draft, Pending, Approved, Active)
- **User Roles**: Assign and manage user permissions (User, Admin)
- **User-Question Relations**: Track quiz attempts and answers

#### Admin Dashboard
- **Users Overview**: Monitor all users with roles and activity status
- **Active Quizzes**: Real-time monitoring of ongoing quiz sessions
- **Content Analytics**: Statistics on questions, topics, and user engagement

---

## 🛠️ Tech Stack

### Core Framework
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Kotlin** | 1.9.0 | Primary language |
| **Jetpack Compose** | 2024.10.01 | Declarative UI framework |
| **Android SDK** | Min 28, Target 35 | Platform support |
| **Java** | 17 | JVM compatibility |

### Architecture & DI
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Hilt** | 2.51.1 | Dependency injection |
| **ViewModel** | 2.8.7 | UI state management |
| **Navigation Compose** | 1.2.0 | App navigation |
| **StateFlow** | 1.7.3 | Reactive state |

### Networking & Data
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Retrofit** | 2.11.0 | REST API client |
| **OkHttp** | 4.12.0 | HTTP client with interceptors |
| **Gson** | 2.10.1 | JSON serialization |
| **Coroutines** | 1.7.3 | Async operations |

### Security
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Security Crypto** | 1.1.0-alpha06 | Encrypted storage |
| **Android Keystore** | Native | Secure key management |
| **JWT** | - | Token-based authentication |

### Testing
| Technology | Version | Purpose |
|-----------|---------|---------|
| **MockK** | Latest | Mocking framework |
| **Turbine** | Latest | Flow testing |
| **Truth** | Latest | Assertions |
| **MockWebServer** | Latest | API mocking |

---

## 🏗️ Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Screens    │→ │  ViewModels  │→ │  UI States   │      │
│  │ (Composables)│  │  (@Hilt)     │  │ (StateFlow)  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │    Models    │  │ Repositories │  │   Result<T>  │      │
│  │  (Entities)  │  │ (Interfaces) │  │ (Error Type) │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │RepositoryImpl│→ │ API Services │→ │     DTOs     │      │
│  │              │  │  (Retrofit)  │  │ (Mappers)    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                    ┌──────────────┐                          │
│                    │   Security   │                          │
│                    │ TokenManager │                          │
│                    └──────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

### Project Structure

```
app/src/main/java/com/app/quizapp/
├── di/                         # Dependency Injection Modules
│   ├── NetworkModule.kt        # Retrofit, OkHttp, Gson
│   ├── RepositoryModule.kt     # Repository bindings
│   └── SecurityModule.kt       # Token manager
│
├── data/                       # Data Layer
│   ├── remote/                 # Network & API
│   │   ├── *ApiService.kt      # 10 Retrofit interfaces
│   │   └── dto/                # 18 Data Transfer Objects
│   ├── repository/             # Repository implementations
│   └── security/               # Auth interceptor, token storage
│
├── domain/                     # Domain Layer
│   ├── model/                  # Business models (8 entities)
│   ├── repository/             # Repository interfaces
│   ├── security/               # TokenManager interface
│   └── util/                   # Result sealed class
│
├── presentation/               # Presentation Layer
│   ├── [feature]/              # User features
│   │   ├── *Screen.kt          # Composable UI
│   │   ├── *ViewModel.kt       # State management
│   │   └── *Destination.kt     # Navigation routes
│   │
│   └── admin/                  # Admin features (ROLE_admin)
│       ├── [entity]/           # CRUD screens for:
│       │   ├── user/           # User management
│       │   ├── topic/          # Topic management
│       │   ├── question/       # Question management
│       │   ├── answer/         # Answer management
│       │   ├── difficulty/     # Difficulty levels
│       │   ├── status/         # Status workflow
│       │   ├── userrole/       # Role management
│       │   └── userquestion/   # Quiz attempts
│       │
│       ├── GenerateQuizzesScreen.kt  # AI quiz generation
│       ├── UsersScreen.kt            # User overview
│       └── ActiveQuizScreen.kt       # Active quizzes
│
├── navigation/                 # Navigation System
│   ├── ApplicationNavGraph.kt  # NavHost configuration
│   └── NavigationDestination.kt# Route interface
│
├── ui/theme/                   # Material3 Theme
├── QuizApplication.kt          # @HiltAndroidApp
└── MainActivity.kt             # @AndroidEntryPoint
```

### Data Flow Pattern

```
User Action (UI)
    ↓
Screen (Composable)
    ↓
ViewModel (viewModelScope.launch)
    ↓
Repository Interface (domain)
    ↓
Repository Implementation (data)
    ↓
API Service (Retrofit)
    ↓
Auth Interceptor (adds JWT token)
    ↓
Backend (Spring Boot) ← → Database
    ↓
DTO Response
    ↓
Domain Model (mapping)
    ↓
Result<T> (Success/Error)
    ↓
ViewModel updates StateFlow
    ↓
UI recomposes with new state
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: Java 17
- **Android SDK**: API 28-35
- **Backend**: Spring Boot backend running (see backend repository)
- **Ollama** (optional): For AI quiz generation features

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/QuizAppHtl.git
   cd QuizAppHtl
   ```

2. **Open in Android Studio**
   ```bash
   # Open the project folder in Android Studio
   # Wait for Gradle sync to complete
   ```

3. **Configure Backend URL**

   Edit `app/src/main/java/com/app/quizapp/di/NetworkModule.kt`:

   ```kotlin
   // For Android Emulator
   private const val BASE_URL = "http://10.0.2.2:8080/"

   // For Physical Device (replace with your computer's IP)
   // private const val BASE_URL = "http://192.168.1.XXX:8080/"
   ```

4. **Build the project**
   ```bash
   ./gradlew build
   ```

5. **Run on Emulator or Device**
   ```bash
   ./gradlew installDebug
   # Or use Android Studio's Run button
   ```

---

## 📱 Usage

### First Time Setup

1. **Launch the app** - You'll see the Cover/Splash screen
2. **Create an account** - Tap "Register" and fill in your details:
   - First name, Last name
   - Email address
   - Birthdate
   - Password (min 6 characters)
   - Optional nickname
3. **Login** - Use your email and password
4. **Explore quizzes** - Browse categories, topics, and start taking quizzes!

### Taking a Quiz

1. From the **Home Screen**, select a category
2. Choose a **Topic** and **Subtopic**
3. Start the quiz
4. Select your answer for each question
5. Get instant feedback (correct/incorrect)
6. View your final score and review answers

### Admin Features (ROLE_admin only)

1. **Login with admin credentials**
2. Access the **Admin Panel** from the home screen
3. **Generate Quizzes with AI**:
   - Go to "Generate Quizzes"
   - Select Ollama model
   - Enter topic prompt
   - Click "Generate"
   - Review and save to database
4. **Manage Content**:
   - Navigate to any entity screen (Users, Topics, Questions, etc.)
   - Create new items with the "+" button
   - Edit or delete existing items
   - Monitor user activity

---

## 🔐 Security

### Authentication Flow

```
1. User Login → POST /api/auth/login
2. Backend validates credentials
3. JWT token generated and returned
4. Token encrypted with AES256-GCM
5. Token stored in EncryptedSharedPreferences
6. AuthInterceptor adds token to all requests automatically
7. Backend validates token for protected endpoints
```

### Token Storage

- **Encryption**: AES256-GCM with Android Keystore
- **Storage**: EncryptedSharedPreferences (application-scoped)
- **Lifecycle**: Save on login, use for API calls, clear on logout
- **Thread-Safe**: Coroutines with IO dispatcher

### Network Security

- **Development**: HTTP allowed for `localhost` and `10.0.2.2` (emulator)
- **Production**: Configure HTTPS in `network_security_config.xml`
- **Interceptors**:
  - AuthInterceptor (adds JWT token)
  - HttpLoggingInterceptor (debugging)

### Role-Based Access Control

| Endpoint | Role Required |
|----------|---------------|
| `/api/auth/**` | Public |
| `/api/users/me/**` | Authenticated |
| `/api/users/**` (CRUD) | ROLE_admin |
| `/api/llm/**` | ROLE_admin |
| `/api/questions/**` (GET) | Authenticated |
| `/api/questions/**` (POST/PUT/DELETE) | ROLE_admin |

---

## 🧪 Testing

### Run Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests "com.app.quizapp.YourTestClass"
```

### Test Coverage

- **ViewModels**: State management and business logic
- **Repositories**: Data layer integration with MockWebServer
- **Use Cases**: Domain layer business rules
- **UI Components**: Composable testing with ComposeTestRule

---

## 🛠️ Build & Debug

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease

# Clean build
./gradlew clean build

# Install on connected device
./gradlew installDebug
```

### Debugging Network Calls

1. Open **Logcat** in Android Studio
2. Filter by tag: `OkHttp`
3. View request/response bodies, headers, and timing

```
# Example Logcat filter
tag:OkHttp level:DEBUG
```

### Common Issues

| Issue | Solution |
|-------|----------|
| Network error | Check backend is running at correct URL |
| Token expired | Re-login to get new JWT token |
| Data not loading | Check Logcat for API errors |
| Build fails | Run `./gradlew clean build` |

---

