package com.app.quizapp.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.quizapp.domain.model.QuizQuestion
import com.app.quizapp.domain.repository.QuizRepository
import com.app.quizapp.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State für den Quiz-Screen
 *
 * Diese data class repräsentiert den gesamten Zustand des Quiz-Screens.
 * Alle UI-relevanten Daten sind hier zentral gespeichert.
 *
 * Vorteile eines zentralen UI State:
 * - Single Source of Truth
 * - Einfach zu testen
 * - Konfigurationsänderungen (z.B. Rotation) überleben automatisch
 * - Klare Trennung zwischen UI und Business-Logik
 *
 * @property questions Liste der geladenen Fragen
 * @property currentQuestionIndex Index der aktuellen Frage
 * @property score Aktueller Score des Spielers
 * @property isLoading Wird gerade geladen?
 * @property error Fehlermeldung (null wenn kein Fehler)
 * @property isQuizFinished Ist das Quiz beendet?
 */
data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isQuizFinished: Boolean = false
) {
    /**
     * Helper Property: Gibt die aktuelle Frage zurück (null wenn keine vorhanden)
     */
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)

    /**
     * Helper Property: Gesamtzahl der Fragen
     */
    val totalQuestions: Int
        get() = questions.size
}

/**
 * ViewModel für den Quiz-Screen
 *
 * Das ViewModel:
 * - Verwaltet den UI-State
 * - Lädt Daten vom Repository
 * - Führt Business-Logik aus (z.B. Score-Berechnung)
 * - Überlebt Konfigurationsänderungen (z.B. Display-Rotation)
 *
 * @HiltViewModel aktiviert Dependency Injection für ViewModels
 * @Inject Constructor: Hilt injiziert automatisch das Repository
 *
 * StateFlow vs LiveData:
 * - StateFlow ist moderner und Kotlin-native
 * - Bessere Integration mit Compose
 * - Typsicher
 */
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    // Private MutableStateFlow (nur ViewModel kann ändern)
    private val _uiState = MutableStateFlow(QuizUiState())

    // Public StateFlow (UI kann nur lesen)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    /**
     * Initialisierung: Lädt automatisch Fragen beim Start
     */
    init {
        loadRandomQuestions()
    }

    /**
     * Lädt zufällige Fragen vom Backend
     *
     * @param count Anzahl der zu ladenden Fragen
     * @param category Optional: Kategorie-Filter
     * @param difficulty Optional: Schwierigkeitsgrad-Filter
     */
    fun loadRandomQuestions(
        count: Int = 10,
        category: String? = null,
        difficulty: String? = null
    ) {
        // viewModelScope: Coroutine Scope der automatisch bei ViewModel.onCleared() cancelled wird
        viewModelScope.launch {
            // Loading State setzen
            _uiState.update { it.copy(isLoading = true, error = null) }

            // API-Call durchführen
            when (val result = repository.getRandomQuestions(count, category, difficulty)) {
                is Result.Success -> {
                    // Erfolg: Fragen in den State übernehmen
                    _uiState.update {
                        it.copy(
                            questions = result.data,
                            isLoading = false,
                            currentQuestionIndex = 0,
                            score = 0,
                            isQuizFinished = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    // Fehler: Fehlermeldung im State speichern
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Wird aufgerufen wenn der User eine Antwort auswählt
     *
     * @param selectedAnswerIndex Index der ausgewählten Antwort
     */
    fun onAnswerSelected(selectedAnswerIndex: Int) {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return

        // Prüfen ob die Antwort korrekt ist
        val isCorrect = selectedAnswerIndex == currentQuestion.correctAnswerIndex

        // State aktualisieren
        _uiState.update { state ->
            val newScore = if (isCorrect) state.score + 1 else state.score
            val nextQuestionIndex = state.currentQuestionIndex + 1
            val isFinished = nextQuestionIndex >= state.totalQuestions

            state.copy(
                score = newScore,
                currentQuestionIndex = nextQuestionIndex,
                isQuizFinished = isFinished
            )
        }
    }

    /**
     * Startet das Quiz neu
     */
    fun restartQuiz() {
        _uiState.update {
            it.copy(
                currentQuestionIndex = 0,
                score = 0,
                isQuizFinished = false,
                error = null
            )
        }
    }

    /**
     * Lädt ein bestimmtes Quiz anhand seiner ID
     *
     * @param quizId Die ID des Quiz
     */
    fun loadQuizById(quizId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.getQuizById(quizId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            questions = result.data.questions,
                            isLoading = false,
                            currentQuestionIndex = 0,
                            score = 0,
                            isQuizFinished = false,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
