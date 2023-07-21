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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

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
enum class RelatedTopicElements {
    @SerialName("delete")
    DELETE,

    @SerialName("clear_topic")
    CLEAR_TOPIC
}

class CourseTopicApiImpl(private val client: HttpClient) : CourseTopicApi {
    override suspend fun create(
        request: CreateCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.post("/courses-topics") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun update(
        topicId: UUID,
        request: UpdateCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.patch("/courses-topics/$topicId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun reorder(
        topicId: UUID,
        request: ReorderCourseTopicRequest
    ): ResponseResult<CourseTopicResponse> {
        return client.put("/courses-topics/$topicId/order") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getById(
        topicId: UUID
    ): ResponseResult<CourseTopicResponse> {
        return client.get("/courses-topics/$topicId").toResult()
    }

    override suspend fun getList(courseId: UUID): ResponseResult<List<CourseTopicResponse>> {
        return client.get("/courses-topics") {
            parameter("course_id", courseId)
        }.toResult()
    }

    override suspend fun delete(
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements
    ): EmptyResponseResult {
        return client.delete("/courses-topics/$topicId") {
            parameter("elements", Json.encodeToString(relatedTopicElements))
        }.toResult()
    }
}