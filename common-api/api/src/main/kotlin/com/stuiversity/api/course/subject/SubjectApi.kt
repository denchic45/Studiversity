package com.stuiversity.api.course.subject

import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import com.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.stuiversity.api.course.subject.model.SubjectResponse
import com.stuiversity.api.course.subject.model.UpdateSubjectRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface SubjectApi {
    suspend fun create(createSubjectRequest: CreateSubjectRequest): ResponseResult<SubjectResponse>

    suspend fun getById(subjectId: UUID): ResponseResult<SubjectResponse>

    suspend fun update(subjectId: UUID, updateSubjectRequest: UpdateSubjectRequest): ResponseResult<SubjectResponse>

    suspend fun search(query: String): ResponseResult<List<SubjectResponse>>

    suspend fun delete(subjectId: UUID): EmptyResponseResult
}

class SubjectApiImpl(private val client: HttpClient) : SubjectApi {
    override suspend fun create(createSubjectRequest: CreateSubjectRequest): ResponseResult<SubjectResponse> {
        return client.post("/subjects") {
            contentType(ContentType.Application.Json)
            setBody(createSubjectRequest)
        }.toResult()
    }

    override suspend fun getById(subjectId: UUID): ResponseResult<SubjectResponse> {
        return client.get("/subjects/$subjectId").toResult()
    }

    override suspend fun update(subjectId: UUID, updateSubjectRequest: UpdateSubjectRequest): ResponseResult<SubjectResponse> {
        return client.patch("/subjects/$subjectId") {
            contentType(ContentType.Application.Json)
            setBody(updateSubjectRequest)
        }.toResult()
    }

    override suspend fun search(query: String): ResponseResult<List<SubjectResponse>> {
        return client.get("/subjects") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun delete(subjectId: UUID): EmptyResponseResult {
        return client.delete("/subjects/$subjectId").toResult()
    }
}