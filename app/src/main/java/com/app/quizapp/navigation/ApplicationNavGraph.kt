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
import com.app.quizapp.presentation.login.LoginDestination
import com.app.quizapp.presentation.login.LoginScreen
import com.app.quizapp.presentation.register.CreateAccountDestination
import com.app.quizapp.presentation.register.CreateAccountScreen
import com.app.quizapp.presentation.home.HomeDestination
import com.app.quizapp.presentation.home.HomeScreen
import com.app.quizapp.presentation.category.CategoryDestination
import com.app.quizapp.presentation.category.CategoryScreen
import com.app.quizapp.presentation.topic.TopicDestination
import com.app.quizapp.presentation.topic.TopicScreen
import com.app.quizapp.presentation.subtopic.SubtopicDestination
import com.app.quizapp.presentation.subtopic.SubtopicScreen
import com.app.quizapp.presentation.profile.ProfileOverviewDestination
import com.app.quizapp.presentation.profile.ProfileOverviewScreen
import com.app.quizapp.presentation.profile.ProfileStatisticsDestination
import com.app.quizapp.presentation.profile.ProfileStatisticsScreen
import com.app.quizapp.presentation.settings.SettingsDestination
import com.app.quizapp.presentation.settings.SettingsScreen
import com.app.quizapp.presentation.quiz.QuizDestination
import com.app.quizapp.presentation.quiz.QuizScreen
import com.app.quizapp.presentation.quiz.QuizResultDestination
import com.app.quizapp.presentation.quiz.QuizResultScreen
import com.app.quizapp.presentation.quiz.QuizReviewDestination
import com.app.quizapp.presentation.quiz.QuizReviewScreen


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

        composable(route = WelcomeDestination.route) {                  // Welcome-Screen
            WelcomeScreen(
                onGetStartedClick = { navController.navigate(LoginDestination.route) },
                onLogInClick = { navController.navigate(LoginDestination.route) }
            )
        }

        composable(route = LoginDestination.route) {                    // Login-Screen
            LoginScreen(
                onLoginClick = { navController.navigate(HomeDestination.route) },
                onCreateAccountClick = { navController.navigate(CreateAccountDestination.route) },
                onForgotPasswordClick = { /* TODO: Navigate to Forgot Password */ }
            )
        }

        composable(route = CreateAccountDestination.route) {           // Create Account-Screen
            CreateAccountScreen(
                onCreateAccountClick = { navController.navigate(HomeDestination.route) },
                onLoginClick = { navController.navigate(LoginDestination.route) },
                onEditAvatarClick = { /* TODO: Open avatar picker */ }
            )
        }

        composable(route = HomeDestination.route) {                        // Home-Screen
            HomeScreen(
                onDailyQuizClick = { navController.navigate(QuizDestination.route) },
                onCategoryClick = { navController.navigate(CategoryDestination.route) },
                onSeeAllCategoriesClick = { navController.navigate(CategoryDestination.route) },
                onSeeAllStatsClick = { navController.navigate(ProfileStatisticsDestination.route) },
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }
        composable(route = CategoryDestination.route) {                 // Categories-Screen als Start-Route
            CategoryScreen(
                onBackClick = { navController.popBackStack()},
                onCategoryClick = { navController.navigate(TopicDestination.route)},
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }

        composable(route = TopicDestination.route) {                    // Topics-Screen
            TopicScreen(
                onBackClick = { navController.popBackStack() },
                onTopicClick = { navController.navigate(SubtopicDestination.route) },
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }

        composable(route = SubtopicDestination.route) {                 // Subtopics-Screen
            SubtopicScreen(
                onBackClick = { navController.popBackStack()},
                onSubtopicClick = { navController.navigate(QuizDestination.route) },
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }

        composable(route = ProfileOverviewDestination.route) {             // Profile Overview-Screen
            ProfileOverviewScreen(
                onEditProfileClick = { /* TODO: Navigate to edit profile */ },
                onStatisticsClick = { navController.navigate(ProfileStatisticsDestination.route) },
                onSettingsClick = { navController.navigate(SettingsDestination.route) },
                onCategoryClick = { navController.navigate(CategoryDestination.route) },
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }

        composable(route = ProfileStatisticsDestination.route) {          // Profile Statistics-Screen
            ProfileStatisticsScreen(
                onEditProfileClick = { /* TODO: Navigate to edit profile */ },
                onOverviewClick = { navController.navigate(ProfileOverviewDestination.route) },
                onSettingsClick = { navController.navigate(SettingsDestination.route) },
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }

        composable(route = SettingsDestination.route) {                   // Settings-Screen
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onPersonalInfoClick = { /* TODO: Navigate to personal info */ },
                onNotificationClick = { /* TODO: Navigate to notification settings */ },
                onSoundClick = { /* TODO: Navigate to sound settings */ },
                onApplyForAdminClick = { /* TODO: Navigate to admin application */ },
                onHelpCenterClick = { /* TODO: Navigate to help center */ },
                onAboutClick = { /* TODO: Navigate to about page */ },
                onLogoutClick = {
                    navController.navigate(WelcomeDestination.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onHomeClick = { navController.navigate(HomeDestination.route) },
                onDiscoverClick = { navController.navigate(CategoryDestination.route) },
                onProfileClick = { navController.navigate(ProfileOverviewDestination.route) }
            )
        }

        composable(route = QuizDestination.route) {                       // Quiz-Screen
            QuizScreen(
                onCloseClick = { navController.navigate(HomeDestination.route) },
                onContinueClick = { currentQuestion, totalQuestions ->
                    // Continue to next question (handled internally in QuizScreen)
                },
                onQuizComplete = { score, totalQuestions ->
                    navController.navigate("${QuizResultDestination.route}/$score/$totalQuestions")
                }
            )
        }

        composable(route = "${QuizResultDestination.route}/{score}/{totalQuestions}") { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val totalQuestions = backStackEntry.arguments?.getString("totalQuestions")?.toIntOrNull() ?: 5

            QuizResultScreen(
                score = score,
                totalQuestions = totalQuestions,
                onReplayClick = {
                    navController.navigate(QuizDestination.route) {
                        popUpTo(QuizDestination.route) { inclusive = true }
                    }
                },
                onReviewClick = {
                    navController.navigate("${QuizReviewDestination.route}/$score/$totalQuestions")
                },
                onContinueClick = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(HomeDestination.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = "${QuizReviewDestination.route}/{score}/{totalQuestions}") { backStackEntry ->
            QuizReviewScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

