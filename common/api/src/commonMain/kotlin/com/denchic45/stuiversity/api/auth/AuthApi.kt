package com.denchic45.stuiversity.api.auth

import com.denchic45.stuiversity.api.auth.model.RefreshTokenRequest
import com.denchic45.stuiversity.api.auth.model.SignInByEmailPasswordRequest
import com.denchic45.stuiversity.api.auth.model.SignInResponse
import com.denchic45.stuiversity.api.auth.model.SignupRequest
import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthApi {
    suspend fun signup(request: SignupRequest): EmptyResponseResult

    suspend fun signInByEmailPassword(request: SignInByEmailPasswordRequest): ResponseResult<SignInResponse>

    suspend fun refreshToken(request: RefreshTokenRequest): ResponseResult<SignInResponse>

    suspend fun recoverPassword(email: String): EmptyResponseResult

    suspend fun checkConfirmCode(code: String): EmptyResponseResult

    suspend fun updateRecoveredPassword(password: String, code: String): EmptyResponseResult
}

class AuthApiImpl(private val client: HttpClient) : AuthApi {
    override suspend fun signup(request: SignupRequest): EmptyResponseResult {
        return client.post("/auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun signInByEmailPassword(request: SignInByEmailPasswordRequest): ResponseResult<SignInResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            setBody(request)
            parameter("grant_type", "password")
        }.toResult()
    }

    override suspend fun refreshToken(request: RefreshTokenRequest): ResponseResult<SignInResponse> {
        return client.post("/auth/token") {
            contentType(ContentType.Application.Json)
            parameter("grant_type", "refresh_token")
            setBody(request)
        }.toResult()
    }

    override suspend fun recoverPassword(email: String): EmptyResponseResult {
        return client.post("/auth/recover-password") {
            contentType(ContentType.Text.Plain)
            setBody(email)
        }.toResult()
    }

    override suspend fun checkConfirmCode(code: String): EmptyResponseResult {
        return client.get("/auth/confirmation-code") {
            parameter("code", code)
        }.toResult()
    }

    override suspend fun updateRecoveredPassword(password: String, code: String): EmptyResponseResult {
        return client.post("/auth/password") {
            parameter("code", code)
        }.toResult()
    }
}