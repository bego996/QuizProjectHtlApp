package com.app.quizapp.presentation.categories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
 * @param imageRes optional drawable resource for the card background image
 * @param leftColor solid color on the left (0%–45% of gradient)
 * @param tintColor semi-transparent tint overlay on the right over the image
 */
data class Category(
    val id: String,
    val name: String,
    val color: Color,
    val isBookmarked: Boolean = false,
    val imageRes: Int? = null,
    val leftColor: Color = Color(0xFF1A1A1A),
    val tintColor: Color = Color(0x801A1A1A)
)

/** Style definition per category: image + gradient colors */
private data class CategoryStyle(
    val imageRes: Int,
    val leftColor: Color,
    val tintColor: Color
)

/** Maps category name keywords (lowercase) to their visual style */
private val categoryStyleMap: Map<String, CategoryStyle> = mapOf(
    "math"       to CategoryStyle(R.drawable.math_albert,  Color(0xFF140202), Color(0x80EA2B2B)),
    "mathematik" to CategoryStyle(R.drawable.math_albert,  Color(0xFF140202), Color(0x80EA2B2B)),
    "maths"      to CategoryStyle(R.drawable.math_albert,  Color(0xFF140202), Color(0x80EA2B2B)),
    "history"    to CategoryStyle(R.drawable.history,      Color(0xFF8B6C00), Color(0x80D4A418)),
    "geschichte" to CategoryStyle(R.drawable.history,      Color(0xFF8B6C00), Color(0x80D4A418))
    // Weitere Kategorien hier ergänzen:
    // "physik" to CategoryStyle(R.drawable.physics, Color(0xFF0D2968), Color(0x801976D2)),
)



/**
 * Main Categories screen showing all available quiz categories
 * Integrates with CategoryViewModel to load real categories
 * Shows a difficulty selection dialog before navigating
 * @param onCategoryClick Called when a category is selected
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
        // Style (Bild + Farben) anhand des Kategorienamens (case-insensitive) suchen
        val style = categoryStyleMap.entries
            .firstOrNull { topic.topic.lowercase().contains(it.key) }?.value
        Category(
            id = topic.topicId.toString(),
            name = topic.topic,
            color = categoryColors[index % categoryColors.size],
            isBookmarked = false,
            imageRes = style?.imageRes,
            leftColor = style?.leftColor ?: categoryColors[index % categoryColors.size],
            tintColor = style?.tintColor ?: categoryColors[index % categoryColors.size].copy(alpha = 0.5f)
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
                currentRoute = CategoryDestination.route,
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
 * Individual category card with image background and dark gradient overlay.
 * If no image is provided, falls back to a solid color gradient (placeholder style).
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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Basisfarbe-Hintergrund (links sichtbar hinter Gradient)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(category.leftColor)
            )

            // Bild rechtsbündig, skaliert auf volle Kartenhöhe → vollständig sichtbar
            if (category.imageRes != null) {
                Image(
                    painter = painterResource(id = category.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    alignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Gradient-Overlay: leftColor opak 0%–45%, Übergang, dann tintColor über dem Bild rechts
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.40f to category.leftColor,
                                0.45f to category.leftColor,
                                0.60f to category.leftColor.copy(alpha = 0f),
                                1.0f to category.tintColor
                            )
                        )
                    )
            )

            // Inhalt: Name links, Bookmark-Icon oben rechts
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    text = category.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                // Grünes Bookmark-Icon oben rechts (immer sichtbar als Dekoration)
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = stringResource(R.string.category_bookmark_description),
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                )
            }
        }
    }
}
