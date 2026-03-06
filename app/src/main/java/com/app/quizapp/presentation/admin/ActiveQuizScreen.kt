package com.app.quizapp.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.R
import com.app.quizapp.domain.model.Answer
import com.app.quizapp.navigation.NavigationDestination

object ActiveQuizDestination : NavigationDestination {
    override val route: String = "active_quiz"
    override val titleRes: Int = R.string.active_quiz
}

enum class QuizStatus {
    ACTIVE,
    INACTIVE
}

data class QuizItem(
    val questionId: Int,
    val id: String,
    val topic: String,
    val question: String,
    val reviewedBy: String?,
    val createdDate: String,
    val status: QuizStatus
)

/**
 * Admin screen for viewing and managing active quizzes
 * Integrates with ActiveQuizViewModel to load real quiz data
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveQuizScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: ActiveQuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Map domain Question to UI QuizItem
    val quizzes = uiState.quizzes.map { question ->
        QuizItem(
            questionId = question.questionId,
            id = "#${question.questionId}",
            topic = question.topic.topic,
            question = question.questionText,
            reviewedBy = question.reviewedBy?.let { if (it > 0) "${question.reviewedBy}" else null },
            createdDate = question.createdAt,
            status = if (question.status.text == "active") QuizStatus.ACTIVE else QuizStatus.INACTIVE
        )
    }

    val expandedQuizIds = uiState.expandedQuizIds
    val answersMap = uiState.answersMap

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(ActiveQuizDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = onHomeClick,
                onDiscoverClick = onDiscoverClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(quizzes) { quiz ->
                QuizItemCard(
                    quiz = quiz,
                    isExpanded = expandedQuizIds.contains(quiz.questionId),
                    answers = answersMap[quiz.questionId] ?: emptyList(),
                    onDetailsClick = { viewModel.toggleQuizExpansion(quiz.questionId) },
                    onDeleteClick = { viewModel.deleteQuiz(quiz.questionId) }
                )
            }
        }
    }
}

/**
 * Individual quiz item card with status badge and action buttons
 * @param quiz Quiz item data
 * @param isExpanded Whether the answers section is expanded
 * @param answers List of answers for this question
 * @param onDetailsClick Callback when details/collapse button is clicked
 * @param onDeleteClick Callback when delete button is clicked
 */
@Composable
fun QuizItemCard(
    quiz: QuizItem,
    isExpanded: Boolean,
    answers: List<Answer>,
    onDetailsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val statusColor = when (quiz.status) {
        QuizStatus.ACTIVE -> Color(0xFF2E7D32)
        QuizStatus.INACTIVE -> Color(0xFFFF8F00)
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Quiz löschen",
            message = "Möchtest du diese Frage wirklich löschen?\n\n\"${quiz.question}\"",
            onConfirm = {
                showDeleteDialog = false
                onDeleteClick()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF006064)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Topic and status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF00838F)
                ) {
                    Text(
                        text = quiz.topic,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor
                ) {
                    Text(
                        text = quiz.status.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question
            Text(
                text = "Question: ${quiz.question}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${quiz.id}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reviewed by
            Text(
                text = "Reviewed by: ${quiz.reviewedBy ?: "None"}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Created date
            Text(
                text = "Erstellt: ${quiz.createdDate}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            // Expandable answers section
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                AnswersSection(
                    answers = answers,
                    onCollapseClick = onDetailsClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isExpanded) {
                    Button(
                        onClick = onDetailsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text(
                            text = "Details ansehen",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Expandable answers section showing all answers with correct/incorrect indicators
 * @param answers List of answers to display
 * @param onCollapseClick Callback when "Weniger anzeigen" button is clicked
 */
@Composable
fun AnswersSection(
    answers: List<Answer>,
    onCollapseClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Answers header
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF00838F)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Answers",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of answers with correct/incorrect indicators
        answers.forEachIndexed { index, answer ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = if (answer.correct) "✓" else "✗",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (answer.correct) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "${index + 1}. ${answer.text}",
                    fontSize = 13.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // "Weniger anzeigen" button
        Button(
            onClick = onCollapseClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00838F)
            )
        ) {
            Text(
                text = "Weniger anzeigen",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

/**
 * Reusable delete confirmation dialog with dark cyan theme
 * @param title Dialog title
 * @param message Dialog message
 * @param onConfirm Callback when user confirms deletion
 * @param onDismiss Callback when user dismisses dialog
 */
@Composable
fun DeleteConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(45.dp)
            ) {
                Text(
                    text = "Ja, löschen",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00838F)
                ),
                modifier = Modifier.height(45.dp)
            ) {
                Text(
                    text = "Abbrechen",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        },
        containerColor = Color(0xFF006064),
        shape = RoundedCornerShape(16.dp)
    )
}
