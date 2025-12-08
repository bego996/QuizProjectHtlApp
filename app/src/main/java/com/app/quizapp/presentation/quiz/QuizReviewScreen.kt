package com.app.quizapp.presentation.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
 */
@Composable
fun QuizReviewScreen(
    onBackClick: () -> Unit = {}
) {
    // Sample review data - will be replaced with ViewModel data
    val reviewQuestions = remember {
        listOf(
            ReviewQuestion(
                questionNumber = 1,
                questionText = "Which festival in Ghana celebrates the harvest season and involves the sprinkling of mashed yams by the paramount chief?",
                topic = "Geographie",
                userAnswer = "Odwira",
                correctAnswer = "Odwira",
                isCorrect = true
            ),
            ReviewQuestion(
                questionNumber = 2,
                questionText = "Which Ghanaian tribe celebrates the annual Kundum Festival, known for its colorful masquerade performances and drumming competitions?",
                topic = "Geographie",
                userAnswer = "Nzema",
                correctAnswer = "Nzema",
                isCorrect = true
            ),
            ReviewQuestion(
                questionNumber = 3,
                questionText = "In traditional Ghanaian marriages, what is the significance of the \"knocking ceremony\"?",
                topic = "Geographie",
                userAnswer = "It symbolizes the formal engagement between families",
                correctAnswer = "It symbolizes the formal engagement between families",
                isCorrect = true
            ),
            ReviewQuestion(
                questionNumber = 4,
                questionText = "What is the name of the traditional festival celebrated by the people of the Ga ethnic group?",
                topic = "Geographie",
                userAnswer = "Odwira",
                correctAnswer = "Homowo",
                isCorrect = false
            ),
            ReviewQuestion(
                questionNumber = 5,
                questionText = "What is the name of the traditional festival celebrated by the people of the Ga ethnic group?",
                topic = "Geographie",
                userAnswer = "Homowo",
                correctAnswer = "Homowo",
                isCorrect = true
            )
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB0E5E0))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF654321),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Review",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321)
                )
            }
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
            text = "Question ${question.questionNumber}",
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
                    contentDescription = "Incorrect",
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
                contentDescription = "Correct",
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
            text = "Topic ${question.topic}",
            fontSize = 12.sp,
            color = Color(0xFFFF8F00),
            fontWeight = FontWeight.Medium
        )

        // Divider
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            thickness = 1.dp,
            color = Color(0xFF80C5C0)
        )
    }
}
