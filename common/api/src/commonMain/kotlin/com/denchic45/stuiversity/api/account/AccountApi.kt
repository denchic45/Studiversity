package com.denchic45.stuiversity.api.account

import com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest
import com.denchic45.stuiversity.api.account.model.UpdateEmailRequest
import com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.toResult
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface AccountApi {
    suspend fun updatePersonal(request: UpdateAccountPersonalRequest): EmptyResponseResult

    suspend fun updateEmail(request: UpdateEmailRequest): EmptyResponseResult

    suspend fun updatePassword(request: UpdatePasswordRequest): EmptyResponseResult
}

class AccountApiImpl(private val client: HttpClient) : AccountApi {
    override suspend fun updatePersonal(request: UpdateAccountPersonalRequest): EmptyResponseResult {
        return client.post("/account/personal") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun updateEmail(request: UpdateEmailRequest): EmptyResponseResult {
        return client.post("/account/email") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun updatePassword(request: UpdatePasswordRequest): EmptyResponseResult {
        return client.post("/account/password") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }
}