# Quiz App - REST API Dokumentation

**Version:** 1.0
**Base URL:** `http://localhost:8080`
**Authentifizierung:** JWT Bearer Token (außer öffentliche Endpoints)

---

## Inhaltsverzeichnis
1. [Authentifizierung & Autorisierung](#authentifizierung--autorisierung)
2. [DTO Übersicht](#dto-übersicht)
3. [Öffentliche Endpoints](#öffentliche-endpoints)
4. [User Endpoints](#user-endpoints)
5. [Admin Endpoints](#admin-endpoints)
6. [AI/LLM Endpoints](#aillm-endpoints)
7. [Sonstige Endpoints](#sonstige-endpoints)

---

## Authentifizierung & Autorisierung

### JWT Token Verwendung
Authentifizierte Requests benötigen einen Bearer Token im Header:
```
Authorization: Bearer <JWT_TOKEN>
```

### Rollen
- **ROLE_user** - Normale Benutzer
- **ROLE_admin** - Administratoren (haben zusätzlich alle User-Rechte)

### Zugriffskontrolle (WebSecurityConfiguration)
```java
// Öffentlich (keine Authentifizierung)
/api/auth/**                           → permitAll

// Nur Admin
/api/llm/**                            → hasRole("admin")
/api/users, /api/users/**              → hasRole("admin") (alle Methoden)
/api/questions (POST, PUT, DELETE)     → hasRole("admin")
/api/userQuestions, /api/userQuestions/** → hasRole("admin")

// Authentifizierte User
/api/users/me/**                       → authenticated()
/api/questions (GET)                   → authenticated()

// Sonstige (keine explizite Regel, Standard: authenticated)
/answers, /topics, /difficulties, /status, /userRoles, /members
```

---

## DTO Übersicht

### Authentication DTOs

#### LoginRequestDto
```java
{
  "email": "user@example.com",      // @Email, @NotBlank
  "password": "password123"          // @NotBlank
}
```

#### RegisterRequestDto
```java
{
  "email": "user@example.com",       // @Email, @NotBlank
  "password": "password123",         // @NotBlank, @Size(min=8)
  "firstname": "Max",                // @NotBlank
  "surname": "Mustermann",           // @NotBlank
  "birthdate": "15.06.2000",         // @NotNull, @Past, Format: dd.MM.yyyy
  "nickname": "maxi"                 // Optional
}
```

#### JwtResponse (Response)
```java
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "email": "user@example.com"
}
```

---

### User Profile DTOs

#### UpdateProfileRequestDto
```java
{
  "firstname": "Max",                // @NotBlank
  "surname": "Mustermann",           // @NotBlank
  "nickname": "maxi",                // Optional
  "birthdate": "2000-06-15"          // @NotNull, LocalDate
}
```

#### ChangePasswordRequestDto
```java
{
  "currentPassword": "oldpass123",   // @NotBlank
  "newPassword": "newpass456"        // @NotBlank, @Size(min=6)
}
```

---

### Quiz DTOs

#### StartQuizRequestDto
```java
{
  "questionId": 42                   // @NotNull
}
```

#### SubmitAnswerRequestDto
```java
{
  "answerId": 123                    // @NotNull
}
```

---

### AI/LLM DTOs

#### GenerateRequestDto
```java
{
  "model": "llama2",                 // String
  "prompt": "Generate a quiz...",    // String
  "stream": false,                   // Boolean
  "format": { ... }                  // JsonNode (optional)
}
```

#### QuizDto
```java
{
  "topic": "Java",                   // @NotBlank
  "question": "Was ist JVM?",        // @NotNull
  "difficulty": "easy",              // @NotNull
  "answers": [                       // @NotEmpty
    "Java Virtual Machine",
    "Java Version Manager",
    "...weitere Antworten"
  ],
  "correct_answer": 0                // @NotNull (Index der korrekten Antwort)
}
```

#### QuizResponseDto (Response)
```java
{
  "model": "llama2",
  "created_at": "2024-01-15T10:30:00",
  "quiz": { ... }                    // QuizDto Objekt
}
```

---

## Öffentliche Endpoints

### 🔓 Authentication

#### POST /api/auth/login
Login mit Email und Passwort, gibt JWT Token zurück.

**Request Body:** `LoginRequestDto`
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** `JwtResponse` (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "email": "user@example.com"
}
```

**Fehler:**
- 401 UNAUTHORIZED - Ungültige Credentials

---

#### POST /api/auth/register
Registrierung eines neuen Users, gibt JWT Token zurück (Auto-Login).

**Request Body:** `RegisterRequestDto`
```json
{
  "email": "newuser@example.com",
  "password": "securepass123",
  "firstname": "Anna",
  "surname": "Schmidt",
  "birthdate": "10.03.1995",
  "nickname": "anna95"
}
```

**Response:** `JwtResponse` (201 CREATED)

**Fehler:**
- 409 CONFLICT - Email existiert bereits
- 500 INTERNAL_SERVER_ERROR - Registrierung fehlgeschlagen

---

## User Endpoints

**Authentifizierung erforderlich!** Alle Endpoints unter `/api/users/me/*`

### 👤 Eigenes Profil

#### GET /api/users/me
Eigenes Profil abrufen.

**Response:** `User` Entity (200 OK)
```json
{
  "userId": 42,
  "email": "user@example.com",
  "firstname": "Max",
  "surname": "Mustermann",
  "nickname": "maxi",
  "birthdate": "2000-06-15",
  "userRole": { ... }
}
```

---

#### PUT /api/users/me
Eigenes Profil aktualisieren (OHNE Passwort/Role).

**Request Body:** `UpdateProfileRequestDto`
```json
{
  "firstname": "Maximilian",
  "surname": "Mustermann",
  "nickname": "max",
  "birthdate": "2000-06-15"
}
```

**Response:** `User` Entity (200 OK)

---

#### PUT /api/users/me/password
Passwort ändern.

**Request Body:** `ChangePasswordRequestDto`
```json
{
  "currentPassword": "oldpass123",
  "newPassword": "newpass456"
}
```

**Response:** String (200 OK)
```json
"Password changed successfully"
```

**Fehler:**
- 401 UNAUTHORIZED - Aktuelles Passwort falsch

---

### 🎯 Quiz Versuche (User)

#### GET /api/users/me/quiz-attempts
Alle eigenen Quiz-Versuche abrufen.

**Response:** `List<UserQuestion>` (200 OK)
```json
[
  {
    "userQuestionId": 1,
    "user": { ... },
    "question": { ... },
    "answer": { ... },
    "score": 100
  }
]
```

---

#### GET /api/users/me/quiz-attempts/{userQuestionId}
Einzelnen eigenen Quiz-Versuch abrufen.

**Path Parameter:** `userQuestionId` (Integer)

**Response:** `UserQuestion` (200 OK)

**Fehler:**
- 404 NOT_FOUND - Quiz-Versuch nicht gefunden oder gehört nicht zum User

---

### 🎮 Quiz spielen

#### POST /api/users/me/quiz/start
Neuen Quiz-Versuch starten.

**Request Body:** `StartQuizRequestDto`
```json
{
  "questionId": 42
}
```

**Response:** `UserQuestion` (201 CREATED)
```json
{
  "userQuestionId": 123,
  "user": { ... },
  "question": { ... },
  "answer": null,
  "score": 0
}
```

**Fehler:**
- 404 NOT_FOUND - Question nicht gefunden

---

#### PUT /api/users/me/quiz/submit/{userQuestionId}
Antwort auf Quiz-Frage submitten.

**Path Parameter:** `userQuestionId` (Integer)
**Request Body:** `SubmitAnswerRequestDto`
```json
{
  "answerId": 456
}
```

**Response:** `UserQuestion` (200 OK)
```json
{
  "userQuestionId": 123,
  "user": { ... },
  "question": { ... },
  "answer": { ... },
  "score": 100
}
```

**Scoring:**
- Richtige Antwort: score = 100
- Falsche Antwort: score = 0

**Fehler:**
- 404 NOT_FOUND - UserQuestion oder Answer nicht gefunden
- 400 BAD_REQUEST - Answer gehört nicht zur Question

---

### 📚 Questions durchsuchen (User)

#### GET /api/questions
Alle verfügbaren Questions abrufen (mit optionalen Filtern).

**Query Parameter (optional):**
- `topicId` (Integer) - Filtern nach Topic
- `difficultyId` (Integer) - Filtern nach Difficulty
- `statusId` (Integer) - Filtern nach Status

**Beispiele:**
```
GET /api/questions
GET /api/questions?topicId=1
GET /api/questions?difficultyId=2&statusId=1
```

**Response:** `List<Question>` (200 OK)

---

#### GET /api/questions/{questionId}
Einzelne Question mit Antworten abrufen.

**Path Parameter:** `questionId` (Integer)

**Response:** `Question` (200 OK)

---

## Admin Endpoints

**Nur für ROLE_admin!**

### 👥 User Management (Admin)

**Base URL:** `/api/users`

#### GET /api/users
Alle User auflisten (max. 10).

**Response:** `List<User>` (200 OK) mit HATEOAS Links

---

#### GET /api/users/{userId}
Einzelnen User abrufen.

**Path Parameter:** `userId` (Integer)

**Response:** `User` (200 OK) mit HATEOAS Links

---

#### POST /api/users
Neuen User erstellen.

**Request Body:** `User` Entity (vollständig)

**Response:** `User` (201 CREATED)

**Fehler:**
- 500 INTERNAL_SERVER_ERROR - Validierungsfehler oder DB-Constraint-Verletzung

---

#### PUT /api/users
User aktualisieren.

**Request Body:** `User` Entity (mit userId)

**Response:** `User` (201 CREATED)

---

#### DELETE /api/users/{userId}
User löschen.

**Path Parameter:** `userId` (Integer)

**Response:** `User` (200 OK) - Gelöschter User

**Fehler:**
- 400 BAD_REQUEST - User nicht gefunden

---

### ❓ Question Management (Admin)

**Base URL:** `/api/questions`

#### POST /api/questions
Neue Question erstellen.

**Request Body:** `Question` Entity

**Response:** `Question` (201 CREATED)

---

#### PUT /api/questions
Question aktualisieren.

**Request Body:** `Question` Entity (mit questionId)

**Response:** `Question` (201 CREATED)

---

#### DELETE /api/questions/{questionId}
Question löschen.

**Path Parameter:** `questionId` (Integer)

**Response:** `Question` (200 OK)

**Fehler:**
- 400 BAD_REQUEST - Question nicht gefunden

---

### 📊 UserQuestion Management (Admin)

**Base URL:** `/api/userQuestions`

Alle CRUD-Operationen für UserQuestions.

#### GET /api/userQuestions
Alle UserQuestions auflisten (max. 10).

**Response:** `List<UserQuestion>` (200 OK)

---

#### GET /api/userQuestions/{userQuestionId}
Einzelne UserQuestion abrufen.

**Response:** `UserQuestion` (200 OK)

---

#### POST /api/userQuestions
Neue UserQuestion erstellen.

**Request Body:** `UserQuestion` Entity

**Response:** `UserQuestion` (201 CREATED)

---

#### PUT /api/userQuestions
UserQuestion aktualisieren.

**Request Body:** `UserQuestion` Entity

**Response:** `UserQuestion` (201 CREATED)

---

#### DELETE /api/userQuestions/{userQuestionId}
UserQuestion löschen.

**Response:** `UserQuestion` (200 OK)

---

### 💡 Weitere Admin CRUD Endpoints

Folgende Entities haben alle Standard-CRUD-Operationen (GET all, GET by ID, POST, PUT, DELETE):

#### Answers - `/api/answers`
- GET /api/answers
- GET /api/answers/{answerId}
- POST /api/answers
- PUT /api/answers
- DELETE /api/answers/{answerId}

#### Topics - `/api/topics`
- GET /api/topics
- GET /api/topics/{topicId}
- POST /api/topics
- PUT /api/topics
- DELETE /api/topics/{topicId}

#### Difficulties - `/api/difficulties`
- GET /api/difficulties
- GET /api/difficulties/{difficultyId}
- POST /api/difficulties
- PUT /api/difficulties
- DELETE /api/difficulties/{difficultyId}

#### Status - `/api/status`
- GET /api/status
- GET /api/status/{statusId}
- POST /api/status
- PUT /api/status
- DELETE /api/status/{statusId}

#### UserRoles - `/api/userRoles`
- GET /api/userRoles
- GET /api/userRoles/{userRoleId}
- POST /api/userRoles
- PUT /api/userRoles
- DELETE /api/userRoles/{userRoleId}

#### Members - `/api/members`
- GET /api/members
- GET /api/members/{memberId}
- POST /api/members
- PUT /api/members
- DELETE /api/members/{memberId}

**Pattern für alle:**
- GET all: Gibt max. 10 Einträge mit HATEOAS Links zurück
- GET by ID: Gibt einzelnes Entity mit HATEOAS Links zurück
- POST/PUT: Request Body = Entity (JSON), Response = Entity mit Status 201 CREATED
- DELETE: Response = Gelöschtes Entity mit Status 200 OK oder 400 BAD_REQUEST

---

## AI/LLM Endpoints

**Nur für ROLE_admin!**
**Base URL:** `/api/llm`

### 🤖 Quiz Generierung mit KI

#### POST /api/llm/quiz/generate
Quiz-Frage mit Ollama generieren (reaktiv).

**Request Body:** `GenerateRequestDto`
```json
{
  "model": "llama2",
  "prompt": "Generate a Java quiz question about inheritance",
  "stream": false,
  "format": { ... }
}
```

**Response:** `Mono<QuizResponseDto>`
```json
{
  "model": "llama2",
  "created_at": "2024-01-15T10:30:00",
  "quiz": {
    "topic": "Java",
    "question": "Was ist Vererbung?",
    "difficulty": "medium",
    "answers": ["...", "...", "..."],
    "correct_answer": 0
  }
}
```

---

#### POST /api/llm/quiz/add
Generiertes Quiz in Datenbank speichern.

**Request Body:** `QuizDto`
```json
{
  "topic": "Java",
  "question": "Was ist Vererbung?",
  "difficulty": "medium",
  "answers": ["Eine Klasse erbt von einer anderen", "..."],
  "correct_answer": 0
}
```

**Response:** `QuizDto` (201 CREATED)

**Fehler:**
- 500 INTERNAL_SERVER_ERROR - DB-Fehler

---

## Sonstige Endpoints

### 🏠 Main/View Controller

**Controller:** `MainController` (Spring MVC, nicht REST)

#### GET /
Forward zu `/index.html`

#### GET /mvc/main
Gibt `main.html` zurück (Thymeleaf Template)

#### GET /logout
Spring Security Logout, Redirect zu `/`

#### GET /error
Error Handler (400, 401, 404, 500)

---

## Allgemeine Hinweise

### HATEOAS Support
Viele Entities enthalten HATEOAS Links:
```json
{
  "userId": 42,
  "email": "user@example.com",
  "_links": {
    "self": { "href": "http://localhost:8080/users/42" },
    "update": { "href": "http://localhost:8080/users" },
    "delete": { "href": "http://localhost:8080/users/42" }
  }
}
```

### Standard Error Responses

**Validation Error (400 BAD_REQUEST):**
```json
"Validation error: [email: Email is required, password: Password is required]"
```

**Server Error (500 INTERNAL_SERVER_ERROR):**
```json
"Error message from exception"
```

### Pagination
Derzeit sind GET-all Endpoints auf 10 Einträge limitiert (`.limit(10)`).

### CORS
`@CrossOrigin` aktiviert für:
- AuthenticationRestController
- UserProfileController
- QuizController

---

## Testing mit cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

### Get Profile (mit Token)
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Start Quiz
```bash
curl -X POST http://localhost:8080/api/users/me/quiz/start \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"questionId":1}'
```

### Submit Answer
```bash
curl -X PUT http://localhost:8080/api/users/me/quiz/submit/123 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"answerId":456}'
```

---

**Erstellt:** 12.12.2025
**Autor:** Bego Jukic
**Hinweis:** Diese Dokumentation basiert auf dem aktuellen Stand des Codes.
