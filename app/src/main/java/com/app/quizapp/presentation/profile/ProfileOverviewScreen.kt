package com.app.quizapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object ProfileOverviewDestination : NavigationDestination {
    override val route: String = "profile_overview"
    override val titleRes: Int = R.string.profile
}

data class Achievement(
    val title: String,
    val description: String
)

/**
 * Profile overview screen showing user info, favourite categories, and achievements
 */
@Composable
fun ProfileOverviewScreen(
    userName: String = "Georgiette Ansaah",
    isAdmin: Boolean = false,
    onEditProfileClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val achievements = listOf(
        Achievement("Bolt", "Completed quiz in less than 2 minutes."),
        Achievement("Genius", "Completed 10 quizzes with perfect score."),
        Achievement("Poised", "Kept a streak going for 10 days."),
        Achievement("Achiever", "Kept a streak going for 15 days.")
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCE775))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321)
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF654321),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
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
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Avatar and name
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFC107)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile avatar",
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "$userName${if (isAdmin) "(Admin)" else ""}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF654321)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Edit Profile button
                    Button(
                        onClick = onEditProfileClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "Edit Profile",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                // Tab selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Overview tab (active)
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00695C)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Overview",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // My Statistics tab
                    Button(
                        onClick = onStatisticsClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFF8E1)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "My Statistics",
                            color = Color(0xFF654321),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                // Favourite Category section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFDCE775))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Favourite Category",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF654321),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Maths category card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(bottom = 12.dp)
                            .clickable { onCategoryClick("Maths") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF8D6E63)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Maths",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Networks category card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable { onCategoryClick("Networks") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF5E35B1)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Networks",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Achievements section header
                Text(
                    text = "Achievements (7)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            items(achievements) { achievement ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFDCE775)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏆",
                            fontSize = 36.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = achievement.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF654321)
                            )
                            Text(
                                text = achievement.description,
                                fontSize = 14.sp,
                                color = Color(0xFF654321).copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}
