package com.stuiversity.api.specialty

import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import com.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.stuiversity.api.specialty.model.SpecialtyResponse
import com.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface SpecialtyApi {
    suspend fun create(createSpecialtyRequest: CreateSpecialtyRequest): ResponseResult<SpecialtyResponse>

    suspend fun getById(specialtyId: UUID): ResponseResult<SpecialtyResponse>

    suspend fun update(
        specialtyId: UUID,
        updateSpecialtyRequest: UpdateSpecialtyRequest
    ): ResponseResult<SpecialtyResponse>

    suspend fun delete(specialtyId: UUID): EmptyResponseResult
}

class SpecialtyApiImpl(private val client: HttpClient) : SpecialtyApi {
    override suspend fun create(createSpecialtyRequest: CreateSpecialtyRequest): ResponseResult<SpecialtyResponse> {
        return client.post("/specialties") {
            contentType(ContentType.Application.Json)
            setBody(createSpecialtyRequest)
        }.toResult()
    }

    override suspend fun getById(specialtyId: UUID): ResponseResult<SpecialtyResponse> {
        return client.get("/specialties/$specialtyId").toResult()
    }

    override suspend fun update(
        specialtyId: UUID,
        updateSpecialtyRequest: UpdateSpecialtyRequest
    ): ResponseResult<SpecialtyResponse> {
        return client.patch("/specialties/$specialtyId") {
            contentType(ContentType.Application.Json)
            setBody(updateSpecialtyRequest)
        }.toResult()
    }

    override suspend fun delete(specialtyId: UUID): EmptyResponseResult {
        return client.delete("/specialties/$specialtyId").toResult()
    }
}