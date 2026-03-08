package com.app.quizapp.di

import android.content.Context
import com.app.quizapp.data.security.SecureTokenManager
import com.app.quizapp.domain.security.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for security-related dependencies
 * Provides TokenManager for secure token storage
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    /**
     * Provides singleton instance of TokenManager
     * Uses SecureTokenManager implementation with EncryptedSharedPreferences
     */
    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return SecureTokenManager(context)
    }
}
