package com.app.quizapp.presentation.cover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background image fills the entire screen
        Image(
            painter = painterResource(id = R.drawable.cover_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
