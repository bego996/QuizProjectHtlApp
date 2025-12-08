package com.app.quizapp.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route: String = "settings"
    override val titleRes: Int = R.string.settings
}

data class SettingsOption(
    val title: String,
    val icon: ImageVector
)

/**
 * Settings screen with various app configuration options
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onPersonalInfoClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSoundClick: () -> Unit = {},
    onApplyForAdminClick: () -> Unit = {},
    onHelpCenterClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onDiscoverClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val settingsOptions = listOf(
        SettingsOption("Personal Info", Icons.Filled.Person),
        SettingsOption("Notification", Icons.Filled.Notifications),
        SettingsOption("Sound", Icons.Filled.Notifications),
        SettingsOption("Apply for Admin", Icons.Filled.Add),
        SettingsOption("Help Center", Icons.Filled.Person),
        SettingsOption("About QuizToGo", Icons.Filled.Info),
        SettingsOption("Logout", Icons.Filled.ExitToApp)
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFB0E5E0))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF654321),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF654321)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF00ACC1),
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Home",
                            tint = Color(0xFF1A1A1A)
                        )
                    },
                    label = {
                        Text(
                            "Home",
                            color = Color(0xFF1A1A1A)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onDiscoverClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Discover",
                            tint = Color(0xFF1A1A1A)
                        )
                    },
                    label = {
                        Text(
                            "Discover",
                            color = Color(0xFF1A1A1A)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onProfileClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile",
                            tint = Color(0xFF1A1A1A)
                        )
                    },
                    label = {
                        Text(
                            "Profile",
                            color = Color(0xFF1A1A1A)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB0E5E0))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(settingsOptions) { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            when (option.title) {
                                "Personal Info" -> onPersonalInfoClick()
                                "Notification" -> onNotificationClick()
                                "Sound" -> onSoundClick()
                                "Apply for Admin" -> onApplyForAdminClick()
                                "Help Center" -> onHelpCenterClick()
                                "About QuizToGo" -> onAboutClick()
                                "Logout" -> onLogoutClick()
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = option.icon,
                                contentDescription = option.title,
                                tint = Color(0xFF654321),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = option.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF654321)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "Navigate",
                            tint = Color(0xFF654321),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
