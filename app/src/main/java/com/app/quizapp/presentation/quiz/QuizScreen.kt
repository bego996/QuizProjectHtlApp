package com.app.quizapp.presentation.quiz

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object QuizDestination : NavigationDestination {
    override val route: String = "quiz"
    override val titleRes: Int = R.string.quiz
}

enum class AnswerState {
    UNSELECTED,
    SELECTED,
    CORRECT,
    INCORRECT
}

/**
 * Quiz screen displaying questions with multiple choice answers
 * Integrates with QuizViewModel to load real questions from backend
 */
@Composable
fun QuizScreen(
    onCloseClick: () -> Unit = {},
    onContinueClick: (Int, Int) -> Unit = { _, _ -> }, // currentQuestion, totalQuestions
    onQuizComplete: (Int, Int) -> Unit = { _, _ -> }, // score, totalQuestions
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle quiz completion
    LaunchedEffect(uiState.isQuizComplete) {
        if (uiState.isQuizComplete) {
            onQuizComplete(uiState.score, uiState.questions.size)
            viewModel.resetQuizComplete()
        }
    }

    // Show loading state
    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF2E7D32))
        }
        return
    }

    // Show error state
    if (uiState.error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = uiState.error ?: stringResource(R.string.quiz_unknown_error),
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(onClick = onCloseClick) {
                    Text(stringResource(R.string.quiz_close_button))
                }
            }
        }
        return
    }

    // Get current question and answers
    val currentQuestion = viewModel.getCurrentQuestion()
    val currentAnswers = viewModel.getCurrentAnswers()

    if (currentQuestion == null || currentAnswers.isEmpty()) {
        return
    }

    val totalQuestions = uiState.questions.size
    var showResult by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableStateOf(13) }
    Log.e("QuizScreen","size of question = $totalQuestions")

    // Reset showResult when question changes
    LaunchedEffect(uiState.currentQuestionIndex) {
        showResult = false
        timeRemaining = 13
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB0E5E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top bar with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.quiz_close_description),
                        tint = Color(0xFF654321),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Timer bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = String.format("00:%02d", timeRemaining),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⏱",
                        fontSize = 24.sp
                    )
                }
            }

            // Question card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.quiz_question_count, uiState.currentQuestionIndex + 1, totalQuestions),
                        fontSize = 14.sp,
                        color = Color(0xFF654321),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.quiz_topic_label, currentQuestion.topic.topic),
                        fontSize = 12.sp,
                        color = Color(0xFF654321)
                    )
                }
            }

            // Answer options
            currentAnswers.forEach { answer ->
                val answerState = when {
                    !showResult && uiState.selectedAnswerId == answer.answerId -> AnswerState.SELECTED
                    showResult && answer.correct -> AnswerState.CORRECT
                    showResult && uiState.selectedAnswerId == answer.answerId && !answer.correct -> AnswerState.INCORRECT
                    else -> AnswerState.UNSELECTED
                }

                AnswerOption(
                    text = answer.text,
                    state = answerState,
                    onClick = {
                        if (!showResult) {
                            viewModel.selectAnswer(answer.answerId)
                        }
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue button
            Button(
                onClick = {
                    if (!showResult && uiState.selectedAnswerId != null) {
                        // Show result for current answer
                        showResult = true
                    } else if (showResult) {
                        // Submit answer and move to next question
                        viewModel.submitAnswer()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                ),
                enabled = uiState.selectedAnswerId != null
            ) {
                Text(
                    text = stringResource(R.string.quiz_continue_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(40.dp)
                .height(4.dp)
                .background(Color.White, RoundedCornerShape(2.dp))
        )
    }
}

/**
 * Answer option card with different states (unselected, selected, correct, incorrect)
 */
@Composable
fun AnswerOption(
    text: String,
    state: AnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (state) {
        AnswerState.UNSELECTED -> Color.White
        AnswerState.SELECTED -> Color(0xFFB2DFDB)
        AnswerState.CORRECT -> Color(0xFF2E7D32)
        AnswerState.INCORRECT -> Color(0xFFD32F2F)
    }

    val textColor = when (state) {
        AnswerState.UNSELECTED, AnswerState.SELECTED -> Color(0xFF1A1A1A)
        AnswerState.CORRECT, AnswerState.INCORRECT -> Color.White
    }

    val borderColor = when (state) {
        AnswerState.SELECTED -> Color(0xFF00695C)
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .border(
                width = if (state == AnswerState.SELECTED) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                // Show icon for correct/incorrect states
                when (state) {
                    AnswerState.CORRECT -> {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = stringResource(R.string.quiz_answer_correct_description),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    AnswerState.INCORRECT -> {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = stringResource(R.string.quiz_answer_incorrect_description),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
