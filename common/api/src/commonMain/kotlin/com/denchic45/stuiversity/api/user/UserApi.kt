package com.denchic45.stuiversity.api.user

import com.denchic45.stuiversity.api.auth.model.CreateUserRequest
import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.user.model.User
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface UserApi {
    suspend fun create(createUserRequest: com.denchic45.stuiversity.api.auth.model.CreateUserRequest): ResponseResult<User>

    suspend fun getMe(): ResponseResult<User>

    suspend fun getById(userId: UUID): ResponseResult<User>

    suspend fun search(query: String): ResponseResult<List<User>>

    suspend fun delete(userId: UUID): EmptyResponseResult
}

class UserApiImpl(private val client: HttpClient) : UserApi {
    override suspend fun create(createUserRequest: com.denchic45.stuiversity.api.auth.model.CreateUserRequest): ResponseResult<User> {
        return client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(createUserRequest)
        }.toResult()
    }

    override suspend fun getMe(): ResponseResult<User> {
        return client.get("/users/me").toResult()
    }

    override suspend fun getById(userId: UUID): ResponseResult<User> {
        return client.get("/users/$userId").toResult()
    }

    override suspend fun search(query: String): ResponseResult<List<User>> {
        return client.get("/users") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun delete(userId: UUID): EmptyResponseResult {
        return client.delete("/users/$userId").toResult()
    }
}