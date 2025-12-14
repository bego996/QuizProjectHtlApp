package com.app.quizapp.presentation.cover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.quizapp.R
import com.app.quizapp.navigation.NavigationDestination
import kotlinx.coroutines.delay

object CoverDestination : NavigationDestination {
    override val route: String = "cover"
    override val titleRes: Int = R.string.cover
}

/**
 * Cover/Splash screen shown briefly when app starts
 * Checks for existing auth token and performs auto-login
 */
@Composable
fun CoverScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToWelcome: () -> Unit = {},
    viewModel: CoverViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation based on auto-login result
    LaunchedEffect(uiState.navigationDestination) {
        when (uiState.navigationDestination) {
            "home" -> {
                viewModel.onNavigationHandled()
                onNavigateToHome()
            }
            "welcome" -> {
                viewModel.onNavigationHandled()
                onNavigateToWelcome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB0E5E0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo (placeholder - replace with actual drawable)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // TODO: Replace with actual logo drawable
                Text(
                    text = "Q",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00ACC1)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App name
            Text(
                text = "QuizToGo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}
