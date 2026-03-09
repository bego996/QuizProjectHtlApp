package com.app.quizapp.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.domain.model.Difficulty
import com.app.quizapp.navigation.NavigationDestination
import com.app.quizapp.R

object TopicDestination : NavigationDestination {
    override val route: String = "topics"
    override val titleRes: Int = R.string.topics
}

/**
 * Data class representing a quiz topic
 */
data class Topic(
    val id: String,
    val name: String,
    val color: Color
)

/**
 * Main Topics screen showing all available topics for a category.
 * Shows a difficulty selection dialog when a topic is clicked.
 * @param onTopicClick Called with (Topic, difficultyId); 0 = Mixed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    onBackClick: () -> Unit = {},
    onTopicClick: (Topic, Int) -> Unit = { _, _ -> },
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: TopicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Pending topic waiting for difficulty selection
    var pendingTopic by remember { mutableStateOf<Topic?>(null) }

    val topicColors = listOf(
        Color(0xFF2D1B1B), Color(0xFFD4A418), Color(0xFFB71C1C), Color(0xFF0D2968),
        Color(0xFF1A1410), Color(0xFF4A5490), Color(0xFF1976D2), Color(0xFF2E7D32),
        Color(0xFF6A1B5A), Color(0xFF4DB6AC)
    )

    val topics = uiState.topics.mapIndexed { index, domainTopic ->
        Topic(
            id = domainTopic.topicId.toString(),
            name = domainTopic.topic,
            color = topicColors[index % topicColors.size]
        )
    }

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = "${uiState.parentCategory} -> ${stringResource(TopicDestination.titleRes)}",
                canNavigateBack = true,
                navigateUp = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = TopicDestination.route,
                onHomeClick = onHomeClick,
                onDiscoverClick = onDiscoverClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = Color(0xFFB0E5E0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(topics) { topic ->
                TopicCard(
                    topic = topic,
                    onClick = { pendingTopic = topic }
                )
            }
        }
    }

    // Difficulty selection dialog shown when a topic is tapped
    pendingTopic?.let { topic ->
        DifficultyDialog(
            topicName = topic.name,
            difficulties = uiState.difficulties,
            onDifficultySelected = { difficultyId ->
                onTopicClick(topic, difficultyId)
                pendingTopic = null
            },
            onDismiss = { pendingTopic = null }
        )
    }
}

/**
 * Dialog for selecting quiz difficulty before entering subtopics.
 * Shows actual difficulties from the backend plus a "Mixed" (all) option.
 * @param topicName Name of the selected topic (shown in title)
 * @param difficulties Difficulty list loaded from backend
 * @param onDifficultySelected Called with difficultyId (0 = Mixed/all)
 * @param onDismiss Called when the dialog is dismissed without selection
 */
@Composable
fun DifficultyDialog(
    topicName: String,
    difficulties: List<Difficulty>,
    onDifficultySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val buttonColors = listOf(
        Color(0xFF2E7D32), Color(0xFFF57F17), Color(0xFFB71C1C),
        Color(0xFF1976D2), Color(0xFF6A1B5A)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = topicName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Wähle eine Schwierigkeit",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                // Dynamic difficulty buttons loaded from backend
                difficulties.forEachIndexed { index, difficulty ->
                    Button(
                        onClick = { onDifficultySelected(difficulty.difficultyId) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColors[index % buttonColors.size]
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = difficulty.mode,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                // Mixed option: no difficulty filter
                Button(
                    onClick = { onDifficultySelected(0) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A5490)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Mixed",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

/**
 * Individual topic card with gradient background
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicCard(
    topic: Topic,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            topic.color,
                            topic.color.copy(alpha = 0.7f),
                            topic.color.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                text = topic.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}
