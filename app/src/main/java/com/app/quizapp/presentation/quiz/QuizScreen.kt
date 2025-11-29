package com.app.quizapp.presentation.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.quizapp.domain.model.QuizQuestion

/**
 * QuizScreen - Haupt-Composable für das Quiz
 *
 * Dieser Screen:
 * - Holt sich das ViewModel via Hilt
 * - Sammelt den UI-State als Compose State
 * - Rendert die UI basierend auf dem State
 * - Delegiert User-Actions an das ViewModel
 *
 * @param viewModel Das QuizViewModel (wird automatisch von Hilt injiziert)
 */
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel()
) {
    // UI State vom ViewModel sammeln
    // collectAsStateWithLifecycle ist Lifecycle-aware und cancelled automatisch
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // UI basierend auf dem State rendern
    QuizContent(
        uiState = uiState,
        onAnswerSelected = viewModel::onAnswerSelected,
        onRestartQuiz = viewModel::restartQuiz,
        onRetry = { viewModel.loadRandomQuestions() }
    )
}

/**
 * QuizContent - Stateless Composable für die UI
 *
 * Diese Composable ist komplett stateless und rendert nur basierend
 * auf den übergebenen Parametern. Dadurch ist sie:
 * - Einfach zu testen
 * - Wiederverwendbar
 * - Vorhersehbar
 *
 * @param uiState Der aktuelle UI-State
 * @param onAnswerSelected Callback wenn User eine Antwort wählt
 * @param onRestartQuiz Callback zum Neustarten
 * @param onRetry Callback zum erneuten Laden bei Fehler
 */
@Composable
fun QuizContent(
    uiState: QuizUiState,
    onAnswerSelected: (Int) -> Unit,
    onRestartQuiz: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Loading State: Zeige Ladebalken
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            // Error State: Zeige Fehlermeldung mit Retry-Button
            uiState.error != null -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = onRetry
                )
            }

            // Quiz beendet: Zeige Ergebnis
            uiState.isQuizFinished -> {
                ScoreScreen(
                    score = uiState.score,
                    totalQuestions = uiState.totalQuestions,
                    onRestart = onRestartQuiz
                )
            }

            // Aktive Frage: Zeige Frage mit Antworten
            uiState.currentQuestion != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Fortschrittsanzeige
                    Text(
                        text = "Frage ${uiState.currentQuestionIndex + 1} von ${uiState.totalQuestions}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LinearProgressIndicator(
                        progress = { (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                    )

                    // Aktueller Score
                    Text(
                        text = "Aktueller Score: ${uiState.score}",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Frage und Antworten
                    QuestionCard(
                        question = uiState.currentQuestion!!,
                        onAnswerSelected = onAnswerSelected
                    )
                }
            }

            // Keine Fragen geladen
            else -> {
                Text(
                    text = "Keine Fragen verfügbar",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * QuestionCard - Zeigt eine Frage mit Antwortmöglichkeiten
 *
 * @param question Die anzuzeigende Frage
 * @param onAnswerSelected Callback wenn eine Antwort gewählt wird
 */
@Composable
fun QuestionCard(
    question: QuizQuestion,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Kategorie und Schwierigkeitsgrad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = question.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = question.difficulty,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Frage
            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Antwortmöglichkeiten
            question.answers.forEachIndexed { index, answer ->
                Button(
                    onClick = { onAnswerSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = answer)
                }
            }
        }
    }
}

/**
 * ScoreScreen - Zeigt das Endergebnis
 *
 * @param score Erreichter Score
 * @param totalQuestions Gesamtzahl der Fragen
 * @param onRestart Callback zum Neustarten
 */
@Composable
fun ScoreScreen(
    score: Int,
    totalQuestions: Int,
    onRestart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Quiz beendet!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Ergebnis mit Prozentangabe
        val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        Text(
            text = "Dein Ergebnis: $score / $totalQuestions",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.titleLarge,
            color = when {
                percentage >= 80 -> MaterialTheme.colorScheme.primary
                percentage >= 50 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.error
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(onClick = onRestart) {
            Text(text = "Nochmal spielen")
        }
    }
}

/**
 * ErrorScreen - Zeigt eine Fehlermeldung
 *
 * @param message Die Fehlermeldung
 * @param onRetry Callback zum erneuten Versuchen
 */
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Fehler",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(onClick = onRetry) {
            Text(text = "Erneut versuchen")
        }
    }
}
