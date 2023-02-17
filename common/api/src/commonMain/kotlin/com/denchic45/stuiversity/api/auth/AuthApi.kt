package com.denchic45.stuiversity.api.auth

import com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.auth.model.TokenResponse
import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthApi {
    suspend fun signup(signupRequest: com.denchic45.stuiversity.api.auth.model.SignupRequest): EmptyResponseResult

    suspend fun signInByEmailPassword(signInByEmailPasswordRequest: com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest): ResponseResult<com.denchic45.stuiversity.api.auth.model.TokenResponse>

    suspend fun refreshToken(refreshTokenRequest: com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest): ResponseResult<com.denchic45.stuiversity.api.auth.model.TokenResponse>
}

class AuthApiImpl(private val client: HttpClient) : com.denchic45.stuiversity.api.auth.AuthApi {
    override suspend fun signup(signupRequest: com.denchic45.stuiversity.api.auth.model.SignupRequest): EmptyResponseResult {
        return client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(signupRequest)
        }.toResult()
    }

    override suspend fun signInByEmailPassword(signInByEmailPasswordRequest: com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest): ResponseResult<com.denchic45.stuiversity.api.auth.model.TokenResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(signInByEmailPasswordRequest)
            parameter("grant_type", "password")
        }.toResult()
    }

    override suspend fun refreshToken(refreshTokenRequest: com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest): ResponseResult<com.denchic45.stuiversity.api.auth.model.TokenResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            parameter("grant_type", "refresh_token")
            setBody(refreshTokenRequest)
        }.toResult()
    }
}