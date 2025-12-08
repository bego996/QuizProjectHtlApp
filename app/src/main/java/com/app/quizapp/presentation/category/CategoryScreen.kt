package com.app.quizapp.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onBackClick: () -> Unit = {},
    onCategoryClick: (Category) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Sample categories - will be replaced with ViewModel data
    val categories = listOf(
        Category("1", "Maths", Color(0xFF2D1B1B)),
        Category("2", "History", Color(0xFFD4A418)),
        Category("3", "Chemistry", Color(0xFFB71C1C), isBookmarked = true),
        Category("4", "Biology", Color(0xFF0D2968), isBookmarked = true),
        Category("5", "Networks", Color(0xFF1A1410)),
        Category("6", "Informatics", Color(0xFF4A5490)),
        Category("7", "Geography", Color(0xFF1976D2), isBookmarked = true),
        Category("8", "Medicine", Color(0xFF2E7D32), isBookmarked = true),
        Category("9", "Electrical\nengineering", Color(0xFF6A1B5A), isBookmarked = true),
        Category("10", "Customized", Color(0xFF4DB6AC), isBookmarked = true)
    )

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(CategoryDestination.titleRes),
                canNavigateBack = true
            )
//            TopAppBar(
//                title = {
//                    Text(
//                        text = stringResource(CategoryDestination.titleRes),
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        color = Color(0xFF1A1A1A)
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color(0xFF1A1A1A)
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color(0xFFB0E5E0)
//                )
//            )
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

/**
 * Bottom navigation bar with Home, Discover, and Profile tabs
 */
@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onDiscoverClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF00ACC1),
        contentColor = Color.White,
        modifier = Modifier.height(80.dp)
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Home", fontSize = 12.sp) },
            selected = false,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF004D56),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF004D56),
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Discover",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Discover", fontSize = 12.sp) },
            selected = true,
            onClick = onDiscoverClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF004D56),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF004D56),
                indicatorColor = Color(0xFF00796B)
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Profile", fontSize = 12.sp) },
            selected = false,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF004D56),
                selectedTextColor = Color.White,
                unselectedTextColor = Color(0xFF004D56),
                indicatorColor = Color.Transparent
            )
        )
    }
}
