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
    suspend fun signup(signupRequest: SignupRequest): EmptyResponseResult

    suspend fun signInByEmailPassword(signInByEmailPasswordRequest: SignInByEmailPasswordRequest): ResponseResult<TokenResponse>

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): ResponseResult<TokenResponse>
}

class AuthApiImpl(private val client: HttpClient) : AuthApi {
    override suspend fun signup(signupRequest: SignupRequest): EmptyResponseResult {
        return client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(signupRequest)
        }.toResult()
    }

    override suspend fun signInByEmailPassword(signInByEmailPasswordRequest: SignInByEmailPasswordRequest): ResponseResult<TokenResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(signInByEmailPasswordRequest)
            parameter("grant_type", "password")
        }.toResult()
    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): ResponseResult<TokenResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            parameter("grant_type", "refresh_token")
            setBody(refreshTokenRequest)
        }.toResult()
    }
}