package com.denchic45.stuiversity.api.user

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface UserApi {
    suspend fun create(createUserRequest: CreateUserRequest): ResponseResult<UserResponse>

    suspend fun getMe(): ResponseResult<UserResponse>

    suspend fun getById(userId: UUID): ResponseResult<UserResponse>

    suspend fun getBySurname(surname: String): ResponseResult<List<UserResponse>>

    suspend fun search(query: String): ResponseResult<List<UserResponse>>

    suspend fun delete(userId: UUID): EmptyResponseResult
}

class UserApiImpl(private val client: HttpClient) : UserApi {
    override suspend fun create(createUserRequest: CreateUserRequest): ResponseResult<UserResponse> {
        return client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(createUserRequest)
        }.toResult()
    }

    override suspend fun getMe(): ResponseResult<UserResponse> {
        return client.get("/users/me").toResult()
    }

    override suspend fun getById(userId: UUID): ResponseResult<UserResponse> {
        return client.get("/users/$userId").toResult()
    }

    override suspend fun getBySurname(surname: String): ResponseResult<List<UserResponse>> {
        return client.get("/users") {
            parameter("surname",surname)
        }.toResult()
    }

    override suspend fun search(query: String): ResponseResult<List<UserResponse>> {
        return client.get("/users") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun delete(userId: UUID): EmptyResponseResult {
        return client.delete("/users/$userId").toResult()
    }
}