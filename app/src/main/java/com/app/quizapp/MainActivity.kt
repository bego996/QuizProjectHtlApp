package com.app.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.app.quizapp.presentation.answer.AnswerScreen
import com.app.quizapp.presentation.difficulty.DifficultyScreen
import com.app.quizapp.presentation.question.QuestionScreen
import com.app.quizapp.presentation.status.StatusScreen
import com.app.quizapp.presentation.topic.TopicScreen
import com.app.quizapp.presentation.user.UserScreen
import com.app.quizapp.presentation.userrole.UserRoleScreen
import com.app.quizapp.presentation.userquestion.UserQuestionScreen
import com.app.quizapp.ui.theme.QuizToGoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - Einstiegspunkt der App
 *
 * @AndroidEntryPoint aktiviert Dependency Injection für diese Activity.
 * Dadurch können wir ViewModels und andere Dependencies injizieren.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizToGoTheme {
                AppNavigation()
            }
        }
    }
}

enum class Screen(val title: String) {
    ANSWERS("Antworten"),
    QUESTIONS("Fragen"),
    USERS("Benutzer"),
    USER_ROLES("Benutzerrollen"),
    USER_QUESTIONS("Benutzer-Fragen"),
    TOPICS("Themen"),
    STATUSES("Status"),
    DIFFICULTIES("Schwierigkeitsgrade")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.ANSWERS) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menü")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        Screen.entries.forEach { screen ->
                            DropdownMenuItem(
                                text = { Text(screen.title) },
                                onClick = {
                                    currentScreen = screen
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.ANSWERS -> AnswerScreen()
                Screen.QUESTIONS -> QuestionScreen()
                Screen.USERS -> UserScreen()
                Screen.USER_ROLES -> UserRoleScreen()
                Screen.USER_QUESTIONS -> UserQuestionScreen()
                Screen.TOPICS -> TopicScreen()
                Screen.STATUSES -> StatusScreen()
                Screen.DIFFICULTIES -> DifficultyScreen()
            }
        }
    }
}
