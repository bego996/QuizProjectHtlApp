package com.app.quizapp.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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

object CategoryDestination : NavigationDestination {
    override val route: String = "categories"
    override val titleRes: Int = R.string.categories
}


/**
 * Data class representing a quiz category
 */
data class Category(
    val id: String,
    val name: String,
    val color: Color,
    val isBookmarked: Boolean = false
)

/**
 * Main Categories screen showing all available quiz categories
 * Integrates with CategoryViewModel to load real categories
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onBackClick: () -> Unit = {},
    onCategoryClick: (Category) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Map domain Topic to UI Category with colors
    val categoryColors = listOf(
        Color(0xFF2D1B1B), Color(0xFFD4A418), Color(0xFFB71C1C), Color(0xFF0D2968),
        Color(0xFF1A1410), Color(0xFF4A5490), Color(0xFF1976D2), Color(0xFF2E7D32),
        Color(0xFF6A1B5A), Color(0xFF4DB6AC)
    )

    val categories = uiState.categories.mapIndexed { index, topic ->
        Category(
            id = topic.topicId.toString(),
            name = topic.topic,
            color = categoryColors[index % categoryColors.size],
            isBookmarked = false // TODO: Add bookmark functionality if needed
        )
    }

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(CategoryDestination.titleRes),
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
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

/**
 * Individual category card with gradient background and bookmark icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: Category,
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
                            category.color,
                            category.color.copy(alpha = 0.7f),
                            category.color.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Category name
            Text(
                text = category.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            // Bookmark icon
            if (category.isBookmarked) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Bookmarked",
                    tint = Color(0xFF4DB6AC),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                )
            }
        }
    }
}
