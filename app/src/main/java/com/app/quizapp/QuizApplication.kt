package com.app.quizapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application Class für die gesamte App
 *
 * @HiltAndroidApp aktiviert Hilt (Dependency Injection) für die gesamte App.
 * Diese Annotation generiert die notwendigen Hilt-Komponenten und ermöglicht
 * die Dependency Injection in der gesamten Anwendung.
 */
@HiltAndroidApp
class QuizApplication : Application()
