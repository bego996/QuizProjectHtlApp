package com.app.quizapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.app.quizapp.presentation.admin.answer.AnswerScreen
import com.app.quizapp.presentation.admin.difficulty.DifficultyScreen
import com.app.quizapp.presentation.admin.question.QuestionScreen
import com.app.quizapp.presentation.admin.status.StatusScreen
import com.app.quizapp.presentation.admin.topic.TopicScreen
import com.app.quizapp.presentation.admin.user.UserScreen
import com.app.quizapp.presentation.admin.userquestion.UserQuestionScreen
import com.app.quizapp.presentation.admin.userrole.UserRoleScreen
import com.app.quizapp.presentation.categories.CategoryScreen
import com.app.quizapp.ui.theme.QuizToGoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/**
 * MainActivity - Einstiegspunkt der App
 *
 * @AndroidEntryPoint aktiviert Dependency Injection für diese Activity.
 * Dadurch können wir ViewModels und andere Dependencies injizieren.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Erzwingt die App-Sprache basierend auf der Gerätesprache.
     * Deutsch → Deutsch, alle anderen Sprachen → Englisch.
     * Wird bei jedem Activity-Start und Hintergrund-Rückkehr aufgerufen.
     */
    override fun attachBaseContext(newBase: Context) {
        val deviceLanguage = newBase.resources.configuration.locales[0].language
        val appLocale = if (deviceLanguage == "de") Locale("de") else Locale("en")
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(appLocale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizToGoTheme {
                QuizToGoApp()
                //AppNavigation()
            }
        }
    }
}

//wIRD NICHT MEHR GENUTZT WAR NUR FÜR TESTZWECKE
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
