package com.app.quizapp.di

import com.app.quizapp.data.repository.QuizRepositoryImpl
import com.app.quizapp.domain.repository.QuizRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module für Repository-Dependencies
 *
 * Dieses Modul bindet die Repository-Implementierung an das Interface.
 *
 * @Bind wird verwendet, wenn wir ein Interface an eine Implementierung binden wollen.
 * Hilt weiß dann: "Wenn jemand QuizRepository braucht, gib ihm QuizRepositoryImpl"
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Bindet QuizRepositoryImpl an QuizRepository Interface
     *
     * @Bind sagt Hilt: "Nutze QuizRepositoryImpl wenn QuizRepository benötigt wird"
     * @Singleton sorgt dafür, dass nur eine Instanz existiert
     */

    @Binds
    @Singleton
    abstract fun bindQuizRepository(
        quizRepositoryImpl: QuizRepositoryImpl
    ): QuizRepository
}
