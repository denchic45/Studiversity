package com.denchic45.stuiversity.api.studygroup

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface StudyGroupApi {
    suspend fun create(createStudyGroupRequest: CreateStudyGroupRequest): ResponseResult<StudyGroupResponse>

    suspend fun getById(studyGroupId: UUID): ResponseResult<StudyGroupResponse>

    suspend fun getList(
        memberId: UUID? = null,
        roleId: Long? = null,
        specialtyId: UUID? = null,
        academicYear: Int? = null,
        query: String? = null
    ): ResponseResult<List<StudyGroupResponse>>

    suspend fun update(
        studyGroupId: UUID,
        updateStudyGroupRequest: UpdateStudyGroupRequest,
    ): ResponseResult<StudyGroupResponse>

    suspend fun search(query: String): ResponseResult<List<StudyGroupResponse>> =
        getList(query = query)

    suspend fun delete(studyGroupId: UUID): EmptyResponseResult


    suspend fun getByAcademicYear(academicYear: Int): ResponseResult<List<StudyGroupResponse>>
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

    override suspend fun getList(
        memberId: UUID?,
        roleId: Long?,
        specialtyId: UUID?,
        academicYear: Int?,
        query: String?
    ): ResponseResult<List<StudyGroupResponse>> {
        return client.get("/studygroups") {
            parameter("member_id", memberId)
            parameter("role_id", roleId)
            parameter("specialty_id", specialtyId)
            parameter("academic_year", academicYear)
            parameter("q", query)
        }.toResult()
    }

    override suspend fun update(
        studyGroupId: UUID,
        updateStudyGroupRequest: UpdateStudyGroupRequest,
    ): ResponseResult<StudyGroupResponse> {
        return client.put("/studygroups/$studyGroupId") {
            contentType(ContentType.Application.Json)
            setBody(updateStudyGroupRequest)
        }.toResult()
    }

    override suspend fun delete(studyGroupId: UUID): EmptyResponseResult {
        return client.delete("/studygroups/$studyGroupId").toResult()
    }

    override suspend fun getByAcademicYear(academicYear: Int): ResponseResult<List<StudyGroupResponse>> {
        return client.get("/studygroups") {
            parameter("academic_year", academicYear)
        }.toResult()
    }
}