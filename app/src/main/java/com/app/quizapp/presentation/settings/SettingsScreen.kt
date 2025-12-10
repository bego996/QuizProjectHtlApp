package com.app.quizapp.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.app.quizapp.BottomNavigationBar
import com.app.quizapp.QuizTopAppBar
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isAdmin: Boolean = false,
    onBackClick: () -> Unit = {},
    onPersonalInfoClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onSoundClick: () -> Unit = {},
    onApplyForAdminClick: () -> Unit = {},
    onApplyForAdminRemovalClick: () -> Unit = {},
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
        SettingsOption(if (isAdmin) "Apply for Admin Removal" else "Apply for Admin", Icons.Filled.Add),
        SettingsOption("Help Center", Icons.Filled.Person),
        SettingsOption("About QuizToGo", Icons.Filled.Info),
        SettingsOption("Logout", Icons.AutoMirrored.Filled.ExitToApp)
    )

    Scaffold(
        topBar = {
            QuizTopAppBar(
                modifier = Modifier,
                title = stringResource(SettingsDestination.titleRes),
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
                                "Apply for Admin Removal" -> onApplyForAdminRemovalClick()
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
