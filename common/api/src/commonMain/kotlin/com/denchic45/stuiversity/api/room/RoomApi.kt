package com.denchic45.stuiversity.api.room

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface RoomApi {
    suspend fun create(createRoomRequest: CreateRoomRequest): ResponseResult<RoomResponse>

    suspend fun getById(roomId: UUID): ResponseResult<RoomResponse>

    suspend fun getList(query: String): ResponseResult<List<RoomResponse>>

    suspend fun update(roomId: UUID, updateRoomRequest: UpdateRoomRequest): ResponseResult<RoomResponse>

    suspend fun delete(roomId: UUID): EmptyResponseResult
}

class RoomApiImpl(private val client: HttpClient) : RoomApi {
    override suspend fun create(createRoomRequest: CreateRoomRequest): ResponseResult<RoomResponse> {
        return client.post("/rooms") {
            contentType(ContentType.Application.Json)
            setBody(createRoomRequest)
        }.toResult()
    }

    override suspend fun getById(roomId: UUID): ResponseResult<RoomResponse> {
        return client.get("/rooms/$roomId").toResult()
    }

    override suspend fun getList(query: String): ResponseResult<List<RoomResponse>> {
        return client.get("/rooms") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun update(roomId: UUID, updateRoomRequest: UpdateRoomRequest): ResponseResult<RoomResponse> {
        return client.patch("/rooms/$roomId") {
            contentType(ContentType.Application.Json)
            setBody(updateRoomRequest)
        }.toResult()
    }

    override suspend fun delete(roomId: UUID): EmptyResponseResult {
        return client.delete("/rooms/$roomId").toResult()
    }
}