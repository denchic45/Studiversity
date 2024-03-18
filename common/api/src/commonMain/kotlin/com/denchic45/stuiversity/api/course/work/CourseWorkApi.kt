package com.denchic45.stuiversity.api.course.work

import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.util.UUIDWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CourseWorkApi {
    suspend fun create(courseId: UUID, request: CreateCourseWorkRequest): ResponseResult<CourseWorkResponse>

    suspend fun update(workId: UUID, request: UpdateCourseWorkRequest): ResponseResult<CourseWorkResponse>

    suspend fun getById(workId: UUID): ResponseResult<CourseWorkResponse>

    suspend fun getByAuthor(
        authorId: UUIDWrapper,
        late: Boolean? = null,
        submitted: Boolean? = null
    ): ResponseResult<List<CourseWorkResponse>>
}

class CourseWorkApiImpl(private val client: HttpClient) : CourseWorkApi {
    override suspend fun create(
        courseId: UUID,
        request: CreateCourseWorkRequest
    ): ResponseResult<CourseWorkResponse> {
        return client.post("/courses/$courseId/works") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun update(
        workId: UUID,
        request: UpdateCourseWorkRequest,
    ): ResponseResult<CourseWorkResponse> {
        return client.patch("/course-works/$workId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getById(workId: UUID): ResponseResult<CourseWorkResponse> {
        return client.get("/course-works/$workId").toResult()
    }

    override suspend fun getByAuthor(
        authorId: UUIDWrapper,
        late: Boolean?,
        submitted: Boolean?
    ): ResponseResult<List<CourseWorkResponse>> {
        return client.get("/course-works") {
            parameter("late", late)
            parameter("author_id", authorId.value)
            parameter("submitted", submitted)
        }.toResult()
    }
}