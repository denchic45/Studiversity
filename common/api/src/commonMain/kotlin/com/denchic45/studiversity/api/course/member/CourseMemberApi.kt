package com.denchic45.studiversity.api.course.member

import com.denchic45.studiversity.api.common.EmptyResponseResult
import com.denchic45.studiversity.api.common.ResponseResult
import com.denchic45.studiversity.api.common.toResult
import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.api.member.ScopeMember
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CourseMemberApi {
    suspend fun getByCourse(courseId: UUID): ResponseResult<List<ScopeMember>>

    suspend fun enroll(courseId: UUID, body: CreateMemberRequest): EmptyResponseResult

    suspend fun delete(courseId: UUID, memberId: UUID): EmptyResponseResult
}

class CourseMemberApiImpl(private val client: HttpClient) : CourseMemberApi {
    override suspend fun getByCourse(courseId: UUID): ResponseResult<List<ScopeMember>> {
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