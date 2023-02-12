package com.stuiversity.api.account

import com.stuiversity.api.account.model.UpdateAccountPersonalRequest
import com.stuiversity.api.account.model.UpdateEmailRequest
import com.stuiversity.api.account.model.UpdatePasswordRequest
import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AccountApi {
    suspend fun updatePersonal(updateAccountPersonalRequest: UpdateAccountPersonalRequest): EmptyResponseResult

    suspend fun updateEmail(updateEmailRequest: UpdateEmailRequest):EmptyResponseResult

    suspend fun updatePassword(updatePasswordRequest: UpdatePasswordRequest): EmptyResponseResult
}

class AccountApiImpl(private val client: HttpClient) : AccountApi {
    override suspend fun updatePersonal(updateAccountPersonalRequest: UpdateAccountPersonalRequest): EmptyResponseResult {
        return client.post("/account/personal") {
            contentType(ContentType.Application.Json)
            setBody(updateAccountPersonalRequest)
        }.toResult()
    }

    override suspend fun updateEmail(updateEmailRequest: UpdateEmailRequest): EmptyResponseResult {
        return client.post("/account/email") {
            contentType(ContentType.Application.Json)
            setBody(updateEmailRequest)
        }.toResult()
    }

    override suspend fun updatePassword(updatePasswordRequest: UpdatePasswordRequest): EmptyResponseResult {
        return client.post("/account/password") {
            contentType(ContentType.Application.Json)
            setBody(updatePasswordRequest)
        }.toResult()
    }
}