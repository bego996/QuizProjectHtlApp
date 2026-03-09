package com.app.quizapp.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.app.quizapp.domain.security.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import android.content.SharedPreferences
import javax.inject.Singleton

/**
 * Secure implementation of TokenManager using EncryptedSharedPreferences
 * Stores JWT tokens with AES256 encryption backed by Android Keystore
 */
@Singleton
class SecureTokenManager @Inject constructor(@ApplicationContext private val context: Context) : TokenManager {

    companion object {
        private const val PREFS_FILE_NAME = "quiz_app_secure_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    // Lazy initialization of EncryptedSharedPreferences
    private val encryptedPrefs by lazy {
        createEncryptedPrefs()
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        return try {

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

        } catch (e: Exception) {

            // alte kaputte prefs löschen
            context.deleteSharedPreferences(PREFS_FILE_NAME)

            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
    /**
     * Saves the JWT token securely with encryption
     * Uses IO dispatcher to avoid blocking the main thread
     */
    override suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    /**
     * Retrieves the saved JWT token
     * Uses IO dispatcher for consistent behavior
     */
    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        encryptedPrefs.getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Checks if a valid token exists
     */
    override suspend fun hasToken(): Boolean = withContext(Dispatchers.IO) {
        !encryptedPrefs.getString(KEY_AUTH_TOKEN, null).isNullOrEmpty()
    }

    /**
     * Removes the saved token (used during logout)
     */
    override suspend fun clearToken() = withContext(Dispatchers.IO) {
        encryptedPrefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }
}
