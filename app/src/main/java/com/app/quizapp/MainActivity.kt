package com.app.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                QuizToGoApp()
            }
        }
    }
}

//enum class Screen(val title: String) {
//    ANSWERS("Antworten"),
//    QUESTIONS("Fragen"),
//    USERS("Benutzer"),
//    USER_ROLES("Benutzerrollen"),
//    USER_QUESTIONS("Benutzer-Fragen"),
//    TOPICS("Themen"),
//    STATUSES("Status"),
//    DIFFICULTIES("Schwierigkeitsgrade"),
//
//    CATEGORIES("Cattegorien")
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppNavigation() {
//    var currentScreen by remember { mutableStateOf(Screen.ANSWERS) }
//    var menuExpanded by remember { mutableStateOf(false) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(currentScreen.title) },
//                actions = {
//                    IconButton(onClick = { menuExpanded = true }) {
//                        Icon(Icons.Default.Menu, contentDescription = "Menü")
//                    }
//                    DropdownMenu(
//                        expanded = menuExpanded,
//                        onDismissRequest = { menuExpanded = false }
//                    ) {
//                        Screen.entries.forEach { screen ->
//                            DropdownMenuItem(
//                                text = { Text(screen.title) },
//                                onClick = {
//                                    currentScreen = screen
//                                    menuExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            when (currentScreen) {
//                Screen.ANSWERS -> AnswerScreen()
//                Screen.QUESTIONS -> QuestionScreen()
//                Screen.USERS -> UserScreen()
//                Screen.USER_ROLES -> UserRoleScreen()
//                Screen.USER_QUESTIONS -> UserQuestionScreen()
//                Screen.TOPICS -> TopicScreen()
//                Screen.STATUSES -> StatusScreen()
//                Screen.DIFFICULTIES -> DifficultyScreen()
//                Screen.CATEGORIES -> CategoryScreen()
//            }
//        }
//    }
//}
