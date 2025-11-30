package com.app.quizapp.di

import com.app.quizapp.data.repository.AnswerRepositoryImpl
import com.app.quizapp.data.repository.UserRoleRepositoryImpl
import com.app.quizapp.data.repository.DifficultyRepositoryImpl
import com.app.quizapp.data.repository.StatusRepositoryImpl
import com.app.quizapp.data.repository.TopicRepositoryImpl
import com.app.quizapp.data.repository.QuestionRepositoryImpl
import com.app.quizapp.data.repository.UserRepositoryImpl
import com.app.quizapp.data.repository.UserQuestionRepositoryImpl
//import com.app.quizapp.data.repository.QuizRepositoryImpl
import com.app.quizapp.domain.repository.AnswerRepository
import com.app.quizapp.domain.repository.UserRoleRepository
import com.app.quizapp.domain.repository.DifficultyRepository
import com.app.quizapp.domain.repository.StatusRepository
import com.app.quizapp.domain.repository.TopicRepository
import com.app.quizapp.domain.repository.QuestionRepository
import com.app.quizapp.domain.repository.UserRepository
import com.app.quizapp.domain.repository.UserQuestionRepository
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
     * @Binds sagt Hilt: "Nutze QuizRepositoryImpl wenn QuizRepository benötigt wird"
     * @Singleton sorgt dafür, dass nur eine Instanz existiert
     */
    @Binds
    @Singleton
    abstract fun bindAnswerRepository(
        answerRepositoryIml: AnswerRepositoryImpl
    ): AnswerRepository

    @Binds
    @Singleton
    abstract fun bindUserRoleRepository(
        userRoleRepositoryImpl: UserRoleRepositoryImpl
    ): UserRoleRepository

    @Binds
    @Singleton
    abstract fun bindDifficultyRepository(
        difficultyRepositoryImpl: DifficultyRepositoryImpl
    ): DifficultyRepository

    @Binds
    @Singleton
    abstract fun bindStatusRepository(
        statusRepositoryImpl: StatusRepositoryImpl
    ): StatusRepository

    @Binds
    @Singleton
    abstract fun bindTopicRepository(
        topicRepositoryImpl: TopicRepositoryImpl
    ): TopicRepository

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(
        questionRepositoryImpl: QuestionRepositoryImpl
    ): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserQuestionRepository(
        userQuestionRepositoryImpl: UserQuestionRepositoryImpl
    ): UserQuestionRepository
}
