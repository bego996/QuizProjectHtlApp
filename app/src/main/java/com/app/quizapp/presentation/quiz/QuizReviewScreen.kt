package com.app.quizapp.presentation.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
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
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object QuizReviewDestination : NavigationDestination {
    override val route: String = "quiz_review"
    override val titleRes: Int = R.string.quiz_review
}

data class ReviewQuestion(
    val questionNumber: Int,
    val questionText: String,
    val topic: String,
    val userAnswer: String?,
    val correctAnswer: String,
    val isCorrect: Boolean
)

/**
 * Quiz review screen showing all questions with correct/incorrect answers
 * Displays user's answers, correct answers, and question details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizReviewScreen(
    onBackClick: () -> Unit = {}
) {
    // Get cached review data from QuizViewModel
    val reviewQuestions = remember {
        QuizViewModel.getCachedReviewQuestions()
    }

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(QuizReviewDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onBackClick
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
            itemsIndexed(reviewQuestions) { index, question ->
                ReviewQuestionCard(
                    question = question
                )
            }
        }
    }
}

/**
 * Individual review question card showing question, answers, and correctness
 */
@Composable
fun ReviewQuestionCard(
    question: ReviewQuestion
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Question number header
        Text(
            text = stringResource(R.string.review_question_label, question.questionNumber),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF654321),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Question text
        Text(
            text = question.questionText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Show user's answer if incorrect
        if (!question.isCorrect && question.userAnswer != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.review_incorrect_description),
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = question.userAnswer,
                    fontSize = 14.sp,
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Show correct answer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = stringResource(R.string.review_correct_description),
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = question.correctAnswer,
                fontSize = 14.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Medium
            )
        }

        // Topic
        Text(
            text = stringResource(R.string.review_topic_label, question.topic),
            fontSize = 12.sp,
            color = Color(0xFFFF8F00),
            fontWeight = FontWeight.Medium
        )

        // Divider
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            thickness = 1.dp,
            color = Color(0xFF80C5C0)
        )
    }
}
