package com.denchic45.stuiversity.api.room

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

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