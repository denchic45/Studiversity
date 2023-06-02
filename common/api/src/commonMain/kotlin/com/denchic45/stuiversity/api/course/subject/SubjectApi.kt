package com.denchic45.stuiversity.api.course.subject

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
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

interface SubjectApi {
    suspend fun create(createSubjectRequest: CreateSubjectRequest): ResponseResult<SubjectResponse>

    suspend fun getById(subjectId: UUID): ResponseResult<SubjectResponse>

    suspend fun getList(name: String? = null): ResponseResult<List<SubjectResponse>>

    suspend fun update(
        subjectId: UUID,
        updateSubjectRequest: UpdateSubjectRequest
    ): ResponseResult<SubjectResponse>

    suspend fun search(query: String): ResponseResult<List<SubjectResponse>>

    suspend fun delete(subjectId: UUID): EmptyResponseResult

    suspend fun getIconsUrls(): ResponseResult<List<String>>
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

    override suspend fun getList(name: String?): ResponseResult<List<SubjectResponse>> {
        return client.get("/subjects") {
            parameter("q", name)
        }.toResult()
    }

    override suspend fun update(
        subjectId: UUID,
        updateSubjectRequest: UpdateSubjectRequest
    ): ResponseResult<SubjectResponse> {
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

    override suspend fun getIconsUrls(): ResponseResult<List<String>> {
        return client.get("/subjects-icons").toResult()
    }
}