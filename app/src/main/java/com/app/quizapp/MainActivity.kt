package com.app.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.app.quizapp.presentation.quiz.QuizScreen
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // QuizScreen nutzt jetzt das ViewModel und lädt Daten vom Backend
                    QuizScreen()
                }
            }
        }
    }
}
