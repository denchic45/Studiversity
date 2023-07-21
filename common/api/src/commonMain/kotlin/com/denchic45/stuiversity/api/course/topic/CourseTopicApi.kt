package com.denchic45.stuiversity.api.course.topic

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.ReorderCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.UpdateCourseTopicRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import java.util.UUID

interface CourseTopicApi {
    suspend fun create(request: CreateCourseTopicRequest): ResponseResult<CourseTopicResponse>

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

    suspend fun delete(
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements
    ): EmptyResponseResult
}

@Serializable
enum class RelatedTopicElements { DELETE, CLEAR_TOPIC }

class CourseTopicApiImpl(private val client: HttpClient) : CourseTopicApi {
    override suspend fun create(
        request: CreateCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.post("/course-topics") {
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
        return client.get("/course-topics") {
            parameter("course_id", courseId)
        }.toResult()
    }

    override suspend fun delete(
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements
    ): EmptyResponseResult {
        return client.delete("/course-topics/$topicId") {
            parameter("elements", relatedTopicElements)
        }.toResult()
    }
}