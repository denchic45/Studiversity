package com.denchic45.stuiversity.api.user

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UpdateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.util.*

interface UserApi {
    suspend fun create(createUserRequest: CreateUserRequest): ResponseResult<UserResponse>

    suspend fun update(userId: UUID, request: UpdateUserRequest): ResponseResult<UserResponse>

    suspend fun getMe(): ResponseResult<UserResponse>

    suspend fun getById(userId: UUID): ResponseResult<UserResponse>

    suspend fun getBySurname(surname: String): ResponseResult<List<UserResponse>>

    suspend fun getList(query: String): ResponseResult<List<UserResponse>>

    suspend fun updateAvatar(userId: UUID, request: CreateFileRequest): ResponseResult<String>

    suspend fun deleteAvatar(userId: UUID): ResponseResult<String>

    suspend fun delete(userId: UUID): EmptyResponseResult
}

class UserApiImpl(private val client: HttpClient) : UserApi {
    override suspend fun create(createUserRequest: CreateUserRequest): ResponseResult<UserResponse> {
        return client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(createUserRequest)
        }.toResult()
    }

    override suspend fun update(userId: UUID, request: UpdateUserRequest): ResponseResult<UserResponse> {
        return client.put("/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getMe(): ResponseResult<UserResponse> {
        return client.get("/users/me").apply {
            client.plugin(Auth).providers
                .filterIsInstance<BearerAuthProvider>()
                .first()
        }.toResult()
    }

    override suspend fun getById(userId: UUID): ResponseResult<UserResponse> {
        println("REQUEST: GET USER BY ID: $userId")
        return client.get("/users/$userId").toResult()
    }

    override suspend fun getBySurname(surname: String): ResponseResult<List<UserResponse>> {
        return client.get("/users") {
            parameter("surname", surname)
        }.toResult()
    }

    override suspend fun getList(query: String): ResponseResult<List<UserResponse>> {
        return client.get("/users") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun updateAvatar(
        userId: UUID,
        request: CreateFileRequest,
    ): ResponseResult<String> {
        return client.put("/users/$userId/avatar") {
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", request.bytes, Headers.build {
                            append(
                                HttpHeaders.ContentType,
                                ContentType.defaultForFilePath(request.name)
                            )
                            append(HttpHeaders.ContentDisposition, "filename=${request.name}")
                        })
                    }
                )
            )
        }.toResult()
    }

    override suspend fun deleteAvatar(userId: UUID): ResponseResult<String> {
        return client.delete("/users/$userId/avatar").toResult()
    }

    override suspend fun delete(userId: UUID): EmptyResponseResult {
        return client.delete("/users/$userId").toResult()
    }
}