package dev.aurakai.auraframefx.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.network.api.AIAgentApi
import dev.aurakai.auraframefx.network.api.AuthApi
import dev.aurakai.auraframefx.network.api.ThemeApi
import dev.aurakai.auraframefx.network.api.UserApi
import dev.aurakai.auraframefx.utils.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

import dev.aurakai.auraframefx.di.AuthenticatedNetwork

/**
 * Service class that provides access to all API endpoints in the application.
 * Uses Retrofit for network communication and handles API service initialization.
 */
@Singleton
class AuraApiService @Inject constructor(
    @ApplicationContext private val context: Context,
    @AuthenticatedNetwork private var retrofit: Retrofit,
    private val dispatchers: AppCoroutineDispatchers,
) {

    // Lazy initialization of API services
    var authApi: AuthApi = retrofit.create(AuthApi::class.java)
        private set
    var userApi: UserApi = retrofit.create(UserApi::class.java)
        private set
    var aiAgentApi: AIAgentApi = retrofit.create(AIAgentApi::class.java)
        private set
    var themeApi: ThemeApi = retrofit.create(ThemeApi::class.java)
        private set

    /**
     * Updates the base URL for all API requests.
     */
    fun updateBaseUrl(newBaseUrl: String) {
        if (newBaseUrl != retrofit.baseUrl().toString()) {
            retrofit = retrofit.newBuilder()
                .baseUrl(newBaseUrl)
                .build()

            // Recreate API services
            authApi = retrofit.create(AuthApi::class.java)
            userApi = retrofit.create(UserApi::class.java)
            aiAgentApi = retrofit.create(AIAgentApi::class.java)
            themeApi = retrofit.create(ThemeApi::class.java)
        }
    }

    /**
     * Clears all cached responses from the HTTP client.
     */
    suspend fun clearCache() = withContext(dispatchers.io) {
        (retrofit.callFactory() as? okhttp3.OkHttpClient)?.cache?.evictAll()
    }

    companion object {
        const val BASE_URL = "https://api.auraframefx.com/v1/"
    }
}
