package com.app.quizapp.presentation.topic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
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
 * Main Topics screen showing all available topics for a category
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(
    categoryName: String = "Maths",
    onBackClick: () -> Unit = {},
    onTopicClick: (Topic) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Sample topics - will be replaced with ViewModel data
    val topics = listOf(
        Topic("1", "Arithmetic", Color(0xFF2D1B1B)),
        Topic("2", "Geometry", Color(0xFFD4A418)),
        Topic("3", "Algebra", Color(0xFFB71C1C)),
        Topic("4", "Analysis", Color(0xFF0D2968)),
        Topic("5", "Probability", Color(0xFF1A1410)),
        Topic("6", "Logic & Set Theory", Color(0xFFB71C1C)),
        Topic("7", "Applied\nMathematics", Color(0xFF1976D2)),
        Topic("8", "Trigonometry", Color(0xFF2E7D32))
    )

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = "$categoryName -> ${stringResource(TopicDestination.titleRes)}",
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
                    onClick = { onTopicClick(topic) }
                )
            }
        }
    }
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
                            topic.color,
                            topic.color.copy(alpha = 0.7f),
                            topic.color.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Topic name
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