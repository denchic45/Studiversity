package com.denchic45.stuiversity.api.account

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AccountApi {
    suspend fun updatePersonal(updateAccountPersonalRequest: com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest): EmptyResponseResult

    suspend fun updateEmail(updateEmailRequest: com.denchic45.stuiversity.api.account.model.UpdateEmailRequest): EmptyResponseResult

    suspend fun updatePassword(updatePasswordRequest: com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest): EmptyResponseResult
}

class AccountApiImpl(private val client: HttpClient) :
    AccountApi {
    override suspend fun updatePersonal(updateAccountPersonalRequest: com.denchic45.stuiversity.api.account.model.UpdateAccountPersonalRequest): EmptyResponseResult {
        return client.post("/account/personal") {
            contentType(ContentType.Application.Json)
            setBody(updateAccountPersonalRequest)
        }.toResult()
    }

    override suspend fun updateEmail(updateEmailRequest: com.denchic45.stuiversity.api.account.model.UpdateEmailRequest): EmptyResponseResult {
        return client.post("/account/email") {
            contentType(ContentType.Application.Json)
            setBody(updateEmailRequest)
        }.toResult()
    }

    override suspend fun updatePassword(updatePasswordRequest: com.denchic45.stuiversity.api.account.model.UpdatePasswordRequest): EmptyResponseResult {
        return client.post("/account/password") {
            contentType(ContentType.Application.Json)
            setBody(updatePasswordRequest)
        }.toResult()
    }
}