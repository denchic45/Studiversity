package com.denchic45.stuiversity.api.studygroup.member

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.member.ScopeMembers
import com.denchic45.stuiversity.api.member.CreateMemberRequest
import com.denchic45.stuiversity.api.member.ScopeMember
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface StudyGroupMemberApi {
    suspend fun getByStudyGroup(studyGroupId: UUID): ResponseResult<List<ScopeMember>>

    suspend fun create(studyGroupId: UUID, body: CreateMemberRequest): EmptyResponseResult

    suspend fun delete(studyGroupId: UUID, memberId: UUID): EmptyResponseResult
}

class StudyGroupMemberApiImpl(private val client: HttpClient) : StudyGroupMemberApi {

    override suspend fun getByStudyGroup(studyGroupId: UUID): ResponseResult<List<ScopeMember>> {
        return client.get("/studygroups/${studyGroupId}/members").toResult()
    }

    override suspend fun create(studyGroupId: UUID, body: CreateMemberRequest): EmptyResponseResult {
        return client.post("/studygroups/${studyGroupId}/members") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.toResult()
    }

    override suspend fun delete(
        studyGroupId: UUID,
        memberId: UUID
    ): EmptyResponseResult {
        return client.delete("/studygroups/${studyGroupId}/members/$memberId").toResult()
    }
}