package com.app.quizapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.app.quizapp.BottomNavigationBar
import kotlin.math.cos
import kotlin.math.sin

object ProfileStatisticsDestination : NavigationDestination {
    override val route: String = "profile_statistics"
    override val titleRes: Int = R.string.profile
}

/**
 * Profile statistics screen showing detailed quiz performance metrics
 * Integrates with ProfileViewModel to load user data
 */
@Composable
fun ProfileStatisticsScreen(
    onEditProfileClick: () -> Unit = {},
    onOverviewClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Monthly") }

    val userName = viewModel.getDisplayName()
    val isAdmin = viewModel.isAdmin()

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
                currentRoute = ProfileStatisticsDestination.route,
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
                    // Overview tab
                    Button(
                        onClick = onOverviewClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFF8E1)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Overview",
                            color = Color(0xFF654321),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // My Statistics tab (active)
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00695C)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "My Statistics",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item {
                // Statistics content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFDCE775))
                        .padding(16.dp)
                ) {
                    // Streak info
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF8E1)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥",
                                fontSize = 40.sp,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Column {
                                Text(
                                    text = "19 Days",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00695C)
                                )
                                Text(
                                    text = "Streak",
                                    fontSize = 16.sp,
                                    color = Color(0xFF654321)
                                )
                            }
                        }
                    }

                    // Period selector dropdown
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    ) {
                        Button(
                            onClick = { /* TODO: Show dropdown menu */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDCE775)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = selectedPeriod,
                                color = Color(0xFF654321),
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select period",
                                tint = Color(0xFF654321)
                            )
                        }
                    }

                    // Quiz count text
                    Text(
                        text = "You have played a total",
                        fontSize = 16.sp,
                        color = Color(0xFF654321),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = "26 quizzes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6F00)
                        )
                        Text(
                            text = " this month! 🎉",
                            fontSize = 16.sp,
                            color = Color(0xFF654321)
                        )
                    }

                    // Circular progress chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressChart(
                            progress = 0.65f, // 26 out of ~40
                            modifier = Modifier.size(180.dp)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "26",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "quizzes taken",
                                fontSize = 14.sp,
                                color = Color(0xFF654321)
                            )
                        }
                    }

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Perfect Scores card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFDCE775)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🏁",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "14",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF654321)
                                    )
                                }
                                Text(
                                    text = "Perfect Scores",
                                    fontSize = 14.sp,
                                    color = Color(0xFF654321)
                                )
                            }
                        }

                        // Fastest Record card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF00695C)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Notifications,
                                        contentDescription = "Timer",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "01:30",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Fastest Record",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Circular progress chart composable for displaying quiz statistics
 */
@Composable
fun CircularProgressChart(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 24.dp.toPx()
        val diameter = size.minDimension
        val radius = (diameter - strokeWidth) / 2

        // Background circle (light green)
        drawCircle(
            color = Color(0xFFDCE775).copy(alpha = 0.3f),
            radius = radius,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
        )

        // Progress arc (dark green)
        drawArc(
            color = Color(0xFF2E7D32),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            ),
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            )
        )
    }
}
