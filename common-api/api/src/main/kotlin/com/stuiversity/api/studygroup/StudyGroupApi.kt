package com.stuiversity.api.studygroup

import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import com.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.stuiversity.api.studygroup.model.StudyGroupResponse
import com.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface StudyGroupApi {
    suspend fun create(createStudyGroupRequest: CreateStudyGroupRequest): ResponseResult<StudyGroupResponse>

    suspend fun getById(studyGroupId: UUID): ResponseResult<StudyGroupResponse>

    suspend fun update(
        studyGroupId: UUID,
        updateStudyGroupRequest: UpdateStudyGroupRequest
    ): ResponseResult<StudyGroupResponse>

    suspend fun search(query: String): ResponseResult<List<StudyGroupResponse>>

    suspend fun delete(studyGroupId: UUID): EmptyResponseResult
}

class StudyGroupApiImpl(private val client: HttpClient) : StudyGroupApi {
    override suspend fun create(createStudyGroupRequest: CreateStudyGroupRequest): ResponseResult<StudyGroupResponse> {
        return client.post("/studygroups") {
            contentType(ContentType.Application.Json)
            setBody(createStudyGroupRequest)
        }.toResult()
    }

    override suspend fun getById(studyGroupId: UUID): ResponseResult<StudyGroupResponse> {
        return client.get("/studygroups/$studyGroupId").toResult()
    }

    override suspend fun update(
        studyGroupId: UUID,
        updateStudyGroupRequest: UpdateStudyGroupRequest
    ): ResponseResult<StudyGroupResponse> {
        return client.put("/studygroups/$studyGroupId") {
            contentType(ContentType.Application.Json)
            setBody(updateStudyGroupRequest)
        }.toResult()
    }

    override suspend fun search(query: String): ResponseResult<List<StudyGroupResponse>> {
        return client.get("/studygroups") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun delete(studyGroupId: UUID): EmptyResponseResult {
        return client.delete("/studygroups/$studyGroupId").toResult()
    }
}