package com.app.quizapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.quizapp.presentation.cover.CoverDestination
import com.app.quizapp.presentation.cover.CoverScreen
import com.app.quizapp.presentation.welcome.WelcomeDestination
import com.app.quizapp.presentation.welcome.WelcomeScreen
import com.app.quizapp.presentation.category.CategoryDestination
import com.app.quizapp.presentation.category.CategoryScreen
import com.app.quizapp.presentation.topic.TopicDestination
import com.app.quizapp.presentation.topic.TopicScreen
import com.app.quizapp.presentation.subtopic.SubtopicDestination
import com.app.quizapp.presentation.subtopic.SubtopicScreen


@Composable
fun MettingNavHost(                                           // Hauptfunktion für den Navigations-Host
    navController: NavHostController,                         // Controller zur Verwaltung des Navigationsverhaltens
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,                        // Bindet den NavController an den Host
        startDestination = CoverDestination.route,          // Legt die Start-Route fest
        modifier = modifier
    ) {
        composable(route = CoverDestination.route) {                    // Cover/Splash-Screen
            CoverScreen(
                onTimeout = { navController.navigate(WelcomeDestination.route) }
            )
        }

        composable(route = WelcomeDestination.route) {                  // Welcome-Screen (eigentliche Start-Route)
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(CategoryDestination.route) },
                onLogInClick = { /* TODO: Navigate to Login */ }
            )
        }
        composable(route = CategoryDestination.route) {                 // Categories-Screen als Start-Route
            CategoryScreen(
                onBackClick = { navController.popBackStack()},
                onCategoryClick = { navController.navigate(TopicDestination.route)},
                onHomeClick = { /* TODO */ },
                onDiscoverClick = { /*TODO*/},
                onProfileClick = { /* TODO */ }
            )
        }

        composable(route = TopicDestination.route) {                    // Topics-Screen
            TopicScreen(
                onBackClick = { navController.popBackStack() },
                onTopicClick = { navController.navigate(SubtopicDestination.route) },
                onHomeClick = { /* TODO */ },
                onDiscoverClick = { /* TODO */ },
                onProfileClick = { /* TODO */ }
            )
        }

        composable(route = SubtopicDestination.route) {                 // Subtopics-Screen
            SubtopicScreen(
                onBackClick = { navController.popBackStack()},
                onSubtopicClick = { /* TODO */ },
                onHomeClick = { /* TODO */ },
                onDiscoverClick = { /* TODO */ },
                onProfileClick = { /* TODO */ }
            )
        }
    }
}

