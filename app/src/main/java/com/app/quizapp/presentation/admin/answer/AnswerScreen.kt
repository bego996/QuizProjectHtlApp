package com.app.quizapp.presentation.admin.answer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.quizapp.R
import com.app.quizapp.domain.model.Answer

/**
 * AnswerScreen - Hauptscreen zum Anzeigen aller Answers
 */
@Composable
fun AnswerScreen(
    viewModel: AnswerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnswerContent(
        uiState = uiState,
        onRetry = viewModel::loadAllAnswers
    )
}

/**
 * AnswerContent - Stateless Composable für die UI
 */
@Composable
fun AnswerContent(
    uiState: AnswerUiState,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Loading State
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            // Error State
            uiState.error != null -> {
                ErrorScreen(
                    message = uiState.error,
                    onRetry = onRetry
                )
            }

            // Success State - Zeige Liste
            uiState.answers.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.admin_answer_header, uiState.answers.size),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(uiState.answers) { answer ->
                        AnswerCard(answer = answer)
                    }
                }
            }

            // Keine Daten
            else -> {
                Text(
                    text = stringResource(R.string.admin_no_answers),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * AnswerCard - Zeigt eine einzelne Answer mit allen Details
 */
@Composable
fun AnswerCard(answer: Answer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Antwort-Text
            Text(
                text = answer.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Correct/Incorrect Badge
            Text(
                text = if (answer.correct) stringResource(R.string.admin_answer_correct_label) else stringResource(R.string.admin_answer_incorrect_label),
                style = MaterialTheme.typography.labelMedium,
                color = if (answer.correct)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Frage
            Text(
                text = stringResource(R.string.admin_answer_question_label, answer.question.questionText),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Topic, Difficulty, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.admin_answer_topic_label, answer.question.topic.topic),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.admin_answer_level_label, answer.question.difficulty.mode),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = stringResource(R.string.admin_answer_status_label, answer.question.status.text),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * ErrorScreen - Zeigt eine Fehlermeldung
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
            text = stringResource(R.string.admin_error_title),
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
            Text(text = stringResource(R.string.admin_retry_button))
        }
    }
}
