package com.denchic45.stuiversity.api.course.member

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.member.ScopeMembers
import com.denchic45.stuiversity.api.member.CreateMemberRequest
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CourseMemberApi {
    suspend fun getByCourse(courseId: UUID): ResponseResult<ScopeMembers>

    suspend fun enroll(courseId: UUID, body: CreateMemberRequest): EmptyResponseResult

    suspend fun delete(courseId: UUID, memberId: UUID): EmptyResponseResult
}

class CourseMemberApiImpl(private val client: HttpClient) : CourseMemberApi {
    override suspend fun getByCourse(courseId: UUID): ResponseResult<ScopeMembers> {
        return client.get("/course/${courseId}/members").toResult()
    }

    override suspend fun enroll(courseId: UUID, body: CreateMemberRequest): EmptyResponseResult {
        return client.post("/courses/${courseId}/members") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.toResult()
    }

    override suspend fun delete(
        courseId: UUID,
        memberId: UUID
    ): EmptyResponseResult {
        return client.delete("/courses/${courseId}/members/$memberId").toResult()
    }
}