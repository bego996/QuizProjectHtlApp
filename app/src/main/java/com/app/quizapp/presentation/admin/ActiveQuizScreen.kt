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
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.R
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
    val id: String,
    val topic: String,
    val question: String,
    val reviewedBy: String?,
    val createdDate: String,
    val status: QuizStatus
)

/**
 * Admin screen for viewing and managing active quizzes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveQuizScreen(
    onBackClick: () -> Unit = {},
    onDetailsClick: (String) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Sample quiz data - will be replaced with ViewModel data
    val quizzes = remember {
        listOf(
            QuizItem(
                id = "#12345",
                topic = "Geografie",
                question = "Was ist die Hauptstadt von Deutschland?",
                reviewedBy = "Administrator",
                createdDate = "04.12.2025",
                status = QuizStatus.ACTIVE
            ),
            QuizItem(
                id = "#12346",
                topic = "Geografie",
                question = "Was ist die Hauptstadt von Deutschland?",
                reviewedBy = "Mihir",
                createdDate = "04.12.2025",
                status = QuizStatus.INACTIVE
            ),
            QuizItem(
                id = "#12347",
                topic = "Geografie",
                question = "Was ist die Hauptstadt von Deutschland?",
                reviewedBy = null,
                createdDate = "04.12.2025",
                status = QuizStatus.INACTIVE
            ),
            QuizItem(
                id = "#12348",
                topic = "Geografie",
                question = "Was ist die Hauptstadt von Deutschland?",
                reviewedBy = "Mihir",
                createdDate = "04.12.2025",
                status = QuizStatus.ACTIVE
            )
        )
    }

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
                    onDetailsClick = { onDetailsClick(quiz.id) },
                    onDeleteClick = { onDeleteClick(quiz.id) }
                )
            }
        }
    }
}

/**
 * Individual quiz item card with status badge and action buttons
 */
@Composable
fun QuizItemCard(
    quiz: QuizItem,
    onDetailsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val statusColor = when (quiz.status) {
        QuizStatus.ACTIVE -> Color(0xFF2E7D32)
        QuizStatus.INACTIVE -> Color(0xFFFF8F00)
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
                Text(
                    text = "⋮ Mihir",
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

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                Button(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = "Delete anyway",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
