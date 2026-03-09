package com.app.quizapp.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
import com.app.quizapp.navigation.NavigationDestination
import com.app.quizapp.R

object SubtopicDestination : NavigationDestination {
    override val route: String = "subtopics"
    override val titleRes: Int = R.string.subtopics
}

/**
 * Data class representing a quiz subtopic
 */
data class Subtopic(
    val id: String,
    val name: String,
    val color: Color,
    val unsolvedCount: Int = 0
)

/**
 * Main Subtopics screen showing all available subtopics for a topic
 * Integrates with SubtopicViewModel to load real subtopics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtopicScreen(
    onBackClick: () -> Unit = {},
    onSubtopicClick: (Subtopic, Int) -> Unit = { _, _ -> },
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: SubtopicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Color palette – index comes from SubtopicDisplayItem.colorIndex
    val subtopicColors = listOf(
        Color(0xFF2D1B1B), Color(0xFFD4A418), Color(0xFFB71C1C), Color(0xFF0D2968),
        Color(0xFF1A1410), Color(0xFF4A5490), Color(0xFF1976D2), Color(0xFF2E7D32),
        Color(0xFF6A1B5A), Color(0xFF4DB6AC)
    )

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = "${uiState.parentTopic} -> ${stringResource(SubtopicDestination.titleRes)}",
                canNavigateBack = true,
                navigateUp = onBackClick
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = SubtopicDestination.route,
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
            items(uiState.displayItems, key = { it.topicId }) { item ->
                val subtopic = Subtopic(
                    id = item.topicId.toString(),
                    name = item.name,
                    color = subtopicColors[item.colorIndex % subtopicColors.size],
                    unsolvedCount = item.unsolvedCount
                )
                SubtopicCard(
                    subtopic = subtopic,
                    onClick = { onSubtopicClick(subtopic, uiState.difficultyId) }
                )
            }
        }
    }
}

/**
 * Individual subtopic card with gradient background and play icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtopicCard(
    subtopic: Subtopic,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            subtopic.color,
                            subtopic.color.copy(alpha = 0.7f),
                            subtopic.color.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Subtopic name with unsolved question count
            Text(
                text = "${subtopic.name} (${subtopic.unsolvedCount})",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // Play icon
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Start quiz",
                tint = Color(0xFF4DB6AC),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            )
        }
    }
}
