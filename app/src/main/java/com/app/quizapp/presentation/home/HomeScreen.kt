package com.app.quizapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleRes: Int = R.string.home
}

/**
 * Home screen shown after login - displays daily quiz, popular categories, and user stats
 * Integrates with HomeViewModel to load user information
 */
@Composable
fun HomeScreen(
    onDailyQuizClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onSeeAllCategoriesClick: () -> Unit = {},
    onSeeAllStatsClick: () -> Unit = {},
    onActiveUsersClick: () -> Unit = {},
    onActiveQuizClick: () -> Unit = {},
    onGenerateQuizzesClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val userName = uiState.userName
    val isAdmin = uiState.isAdmin

    Scaffold(
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                // Header with greeting and avatar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello $userName${if (isAdmin) "(Admin)" else ""}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF654321)
                        )
                        Text(
                            text = if (isAdmin) "Let's start your quiz now or do administration" else "Let's start your quiz now",
                            fontSize = 14.sp,
                            color = Color(0xFF654321)
                        )
                    }
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFC107)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile avatar",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            item {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search",
                            color = Color(0xFFB0A090)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF654321)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color(0xFF654321)
                    ),
                    singleLine = true
                )
            }

            item {
                // Daily Quiz Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 24.dp)
                        .clickable { onDailyQuizClick() },
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
                                        Color(0xFF006064),
                                        Color(0xFF00838F)
                                    )
                                )
                            )
                    ) {
                        // Decorative circles
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .offset(x = (-20).dp, y = (-20).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8D6E63).copy(alpha = 0.5f))
                                .align(Alignment.TopStart)
                        )
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .offset(x = 20.dp, y = 20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCE775).copy(alpha = 0.6f))
                                .align(Alignment.BottomEnd)
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "Daily Quiz",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "5 Questions",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }

            // Admin-only cards
            if (isAdmin) {
                item {
                    // Active Users card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 12.dp)
                            .clickable { onActiveUsersClick() },
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
                                            Color(0xFF2E7D32),
                                            Color(0xFF43A047)
                                        )
                                    )
                                )
                        ) {
                            // Decorative circles
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .offset(x = (-20).dp, y = (-20).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCE775).copy(alpha = 0.4f))
                                    .align(Alignment.TopStart)
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .offset(x = 20.dp, y = 20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8D6E63).copy(alpha = 0.3f))
                                    .align(Alignment.BottomEnd)
                            )

                            Text(
                                text = "Active Users",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(24.dp)
                            )
                        }
                    }
                }

                item {
                    // Active/Unreviewed Quizzes card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 12.dp)
                            .clickable { onActiveQuizClick() },
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
                                            Color(0xFF2E7D32),
                                            Color(0xFF43A047)
                                        )
                                    )
                                )
                        ) {
                            // Decorative circles
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .offset(x = (-20).dp, y = (-20).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCE775).copy(alpha = 0.4f))
                                    .align(Alignment.TopStart)
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .offset(x = 20.dp, y = 20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8D6E63).copy(alpha = 0.3f))
                                    .align(Alignment.BottomEnd)
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = "Active/Unreviewed",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Quizzes",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                item {
                    // Generate Quizzes card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(bottom = 24.dp)
                            .clickable { onGenerateQuizzesClick() },
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
                                            Color(0xFF2E7D32),
                                            Color(0xFF43A047)
                                        )
                                    )
                                )
                        ) {
                            // Decorative circles
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .offset(x = (-20).dp, y = (-20).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCE775).copy(alpha = 0.4f))
                                    .align(Alignment.TopStart)
                            )
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .offset(x = 20.dp, y = 20.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF8D6E63).copy(alpha = 0.3f))
                                    .align(Alignment.BottomEnd)
                            )

                            Text(
                                text = "Generate Quizzes",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(24.dp)
                            )
                        }
                    }
                }
            }

            item {
                // Popular Categories section header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Popular Categories",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF654321)
                    )
                    Text(
                        text = "See All >",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF654321),
                        modifier = Modifier.clickable { onSeeAllCategoriesClick() }
                    )
                }
            }

            items(listOf("Maths", "Informatics")) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 12.dp)
                        .clickable { onCategoryClick(category) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (category == "Maths") {
                            Color(0xFF8D6E63)
                        } else {
                            Color(0xFF7E57C2)
                        }
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = category,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                // Stats section header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stats",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF654321)
                    )
                    Text(
                        text = "See All >",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF654321),
                        modifier = Modifier.clickable { onSeeAllStatsClick() }
                    )
                }
            }

            item {
                // Stats cards row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Streak card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF00695C)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "19 Days",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Streak",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Badges card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFDCE775)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "7 Badges",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF654321)
                            )
                            Text(
                                text = "Achievements",
                                fontSize = 14.sp,
                                color = Color(0xFF654321).copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }
    }
}
