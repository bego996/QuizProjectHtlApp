package com.app.quizapp.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.R
import com.app.quizapp.data.remote.dto.QuizResponseDto
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.domain.model.Topic
import com.app.quizapp.navigation.NavigationDestination

object GenerateQuizzesDestination : NavigationDestination {
    override val route: String = "generate_quizzes"
    override val titleRes: Int = R.string.generate_quizzes
}

/**
 * Admin screen for generating quizzes with AI
 * Implements dialog-based workflow for difficulty and topic selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateQuizzesScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: GenerateQuizzesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(GenerateQuizzesDestination.titleRes),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues)
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show generated quiz if available
                uiState.generatedQuizResponse?.let { quizResponse ->
                    GeneratedQuizCard(
                        quizResponse = quizResponse,
                        onApplyClick = { viewModel.applyQuizToDatabase() },
                        onNewRequestClick = { viewModel.startNewRequest() }
                    )
                }

                // Show message if no quiz is generated yet
                if (uiState.generatedQuizResponse == null && !uiState.isLoading) {
                    EmptyStateCard(
                        onNewRequestClick = { viewModel.startNewRequest() }
                    )
                }

                // Loading indicator
                if (uiState.isGenerating) {
                    LoadingIndicator()
                }

                // Error message
                uiState.error?.let { error ->
                    ErrorMessage(
                        message = error,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }

            // Difficulty selection dialog (Image 3)
            if (uiState.showDifficultyDialog) {
                DifficultySelectionDialog(
                    difficulties = uiState.difficulties,
                    onDifficultySelected = { viewModel.selectDifficulty(it) },
                    onRandomSelected = { viewModel.selectRandomDifficulty() },
                    onDismiss = { viewModel.hideDifficultyDialog() }
                )
            }

            // Topic selection dialog (Image 4)
            if (uiState.showTopicSelectionDialog) {
                TopicSelectionDialog(
                    currentLevel = uiState.currentSelectionLevel,
                    availableCategories = uiState.availableCategories,
                    availableTopics = uiState.availableTopics,
                    availableSubtopics = uiState.availableSubtopics,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    onTopicSelected = { viewModel.selectTopic(it) },
                    onSubtopicSelected = { viewModel.selectSubtopic(it) },
                    onRandomSelected = {
                        when (uiState.currentSelectionLevel) {
                            TopicSelectionLevel.CATEGORY -> viewModel.selectRandomCategory()
                            TopicSelectionLevel.TOPIC -> viewModel.selectRandomTopic()
                            TopicSelectionLevel.SUBTOPIC -> viewModel.selectRandomSubtopic()
                        }
                    },
                    onBackClick = { viewModel.goBackInSelection() },
                    onDismiss = { viewModel.hideTopicSelectionDialog() }
                )
            }
        }
    }
}

/**
 * Dialog for difficulty selection (Image 3)
 */
@Composable
private fun DifficultySelectionDialog(
    difficulties: List<Difficulty>,
    onDifficultySelected: (Difficulty) -> Unit,
    onRandomSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1A5C5C)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Wähle die Schwierigkeit:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Difficulty buttons grid (2x2)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(difficulties.take(3)) { difficulty ->
                        DifficultyButton(
                            difficulty = difficulty,
                            onClick = { onDifficultySelected(difficulty) }
                        )
                    }

                    // Random button
                    item {
                        Button(
                            onClick = onRandomSelected,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9C27B0)
                            )
                        ) {
                            Text(
                                text = "Random",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual difficulty button
 */
@Composable
private fun DifficultyButton(
    difficulty: Difficulty,
    onClick: () -> Unit
) {
    val backgroundColor = when (difficulty.mode.lowercase()) {
        "easy" -> Color(0xFF4CAF50)
        "medium", "mittel" -> Color(0xFFFF9800)
        "hard", "schwer" -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = difficulty.mode,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

/**
 * Dialog for topic selection with hierarchical levels (Image 4)
 */
@Composable
private fun TopicSelectionDialog(
    currentLevel: TopicSelectionLevel,
    availableCategories: List<Topic>,
    availableTopics: List<Topic>,
    availableSubtopics: List<Topic>,
    onCategorySelected: (Topic) -> Unit,
    onTopicSelected: (Topic) -> Unit,
    onSubtopicSelected: (Topic) -> Unit,
    onRandomSelected: () -> Unit,
    onBackClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1A5C5C)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentLevel != TopicSelectionLevel.CATEGORY) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }

                    Text(
                        text = when (currentLevel) {
                            TopicSelectionLevel.CATEGORY -> "Wähle ein Thema:"
                            TopicSelectionLevel.TOPIC -> "Wähle ein Topic:"
                            TopicSelectionLevel.SUBTOPIC -> "Wähle ein Subtopic:"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Topics grid
                val topicsToShow = when (currentLevel) {
                    TopicSelectionLevel.CATEGORY -> availableCategories
                    TopicSelectionLevel.TOPIC -> availableTopics
                    TopicSelectionLevel.SUBTOPIC -> availableSubtopics
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(topicsToShow) { topic ->
                        TopicChip(
                            topic = topic,
                            onClick = {
                                when (currentLevel) {
                                    TopicSelectionLevel.CATEGORY -> onCategorySelected(topic)
                                    TopicSelectionLevel.TOPIC -> onTopicSelected(topic)
                                    TopicSelectionLevel.SUBTOPIC -> onSubtopicSelected(topic)
                                }
                            }
                        )
                    }
                }

                // Random button
                Button(
                    onClick = onRandomSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    )
                ) {
                    Text(
                        text = "Random",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Individual topic chip
 */
@Composable
private fun TopicChip(
    topic: Topic,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF26A69A)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = topic.topic,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * Card displaying generated quiz (Image 1)
 */
@Composable
private fun GeneratedQuizCard(
    quizResponse: QuizResponseDto,
    onApplyClick: () -> Unit,
    onNewRequestClick: () -> Unit
) {
    val quiz = quizResponse.quiz

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A5C5C)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category and Difficulty chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF26A69A)
                ) {
                    Text(
                        text = quiz.category,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }


                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF26A69A)
                ) {
                    Text(
                        text = quiz.topic,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }


                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF26A69A)
                ) {
                    Text(
                        text = quiz.subtopic,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Difficulty chip
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFF9800)
                ) {
                    Text(
                        text = quiz.difficulty,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Question
            Text(
                text = quiz.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Answers
            quiz.answers.forEachIndexed { index, answer ->
                AnswerItem(
                    answerNumber = index + 1,
                    answerText = answer,
                    isCorrect = index == quiz.correctAnswer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Apply to DB button
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Apply to DB",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                // New Request button
                Button(
                    onClick = onNewRequestClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00ACC1)
                    )
                ) {
                    Text(
                        text = "🔄 New Request",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Individual answer item in quiz card
 */
@Composable
private fun AnswerItem(
    answerNumber: Int,
    answerText: String,
    isCorrect: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${answerNumber}.",
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = answerText,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        if (isCorrect) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Correct answer",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Wrong answer",
                tint = Color(0xFFF44336),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Empty state card when no quiz is generated
 */
@Composable
private fun EmptyStateCard(
    onNewRequestClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "No quiz generated yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A5C5C)
        )

        Button(
            onClick = onNewRequestClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00ACC1)
            )
        ) {
            Text(
                text = "Generate Quiz",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

/**
 * Loading indicator
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF00ACC1)
            )
            Text(
                text = "Generating quiz...",
                fontSize = 14.sp,
                color = Color(0xFF1A5C5C)
            )
        }
    }
}

/**
 * Error message display
 */
@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFCDD2)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}
