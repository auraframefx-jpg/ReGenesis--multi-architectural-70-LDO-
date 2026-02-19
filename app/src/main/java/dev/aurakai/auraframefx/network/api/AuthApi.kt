package dev.aurakai.auraframefx.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    
    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse
    
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<TokenResponse>
    
    @POST("auth/logout")
    suspend fun logout(): LogoutResponse
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600
)

data class LogoutResponse(
    val success: Boolean
)
