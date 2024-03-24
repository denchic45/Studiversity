package com.denchic45.stuiversity.api.course.topic

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.ReorderCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.UpdateCourseTopicRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CourseTopicApi {
    suspend fun create(courseId: UUID, request: CreateCourseTopicRequest): ResponseResult<CourseTopicResponse>

    suspend fun update(
        topicId: UUID,
        request: UpdateCourseTopicRequest
    ): ResponseResult<CourseTopicResponse>

    suspend fun reorder(
        topicId: UUID,
        request: ReorderCourseTopicRequest
    ): ResponseResult<CourseTopicResponse>

    suspend fun getById(topicId: UUID): ResponseResult<CourseTopicResponse>

    suspend fun getList(courseId: UUID): ResponseResult<List<CourseTopicResponse>>

    suspend fun delete(topicId: UUID, withElements: Boolean): EmptyResponseResult
}

class CourseTopicApiImpl(private val client: HttpClient) : CourseTopicApi {
    override suspend fun create(
        courseId: UUID,
        request: CreateCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.post("/course/$courseId/topics") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun update(
        topicId: UUID,
        request: UpdateCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.patch("/course-topics/$topicId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun reorder(
        topicId: UUID,
        request: ReorderCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.put("/course-topics/$topicId/order") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getById(
        topicId: UUID
    ): ResponseResult<CourseTopicResponse> {
        return client.get("/course-topics/$topicId").toResult()
    }

    override suspend fun getList(courseId: UUID): ResponseResult<List<CourseTopicResponse>> {
        return client.get("/course/$courseId/topics").toResult()
    }

    override suspend fun delete(
        topicId: UUID,
        withElements: Boolean
    ): EmptyResponseResult {
        return client.delete("/course-topics/$topicId") {
            parameter("with_elements", withElements)
        }.toResult()
    }
}