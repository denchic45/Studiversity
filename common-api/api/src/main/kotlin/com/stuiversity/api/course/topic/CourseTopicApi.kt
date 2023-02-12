package com.stuiversity.api.course.topic

import com.stuiversity.api.course.topic.model.CreateTopicRequest
import com.stuiversity.api.course.topic.model.TopicResponse
import com.stuiversity.api.course.topic.model.UpdateTopicRequest
import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

interface CourseTopicApi {
    suspend fun create(courseId: UUID, createTopicRequest: CreateTopicRequest): ResponseResult<TopicResponse>

    suspend fun update(
        courseId: UUID,
        topicId: UUID,
        updateTopicRequest: UpdateTopicRequest
    ): ResponseResult<TopicResponse>

    suspend fun getById(courseId: UUID, topicId: UUID): ResponseResult<TopicResponse>

    suspend fun getByCourseId(courseId: UUID): ResponseResult<List<TopicResponse>>

    suspend fun delete(courseId: UUID, topicId: UUID, relatedTopicElements: RelatedTopicElements): EmptyResponseResult
}

@Serializable
enum class RelatedTopicElements {
    @SerialName("delete")
    DELETE,

    @SerialName("clear_topic")
    CLEAR_TOPIC
}

class CourseTopicApiImpl(private val client: HttpClient) : CourseTopicApi {
    override suspend fun create(courseId: UUID, createTopicRequest: CreateTopicRequest): ResponseResult<TopicResponse> {
        return client.post("/courses/$courseId/topics") {
            contentType(ContentType.Application.Json)
            setBody(createTopicRequest)
        }.toResult()
    }

    override suspend fun update(
        courseId: UUID,
        topicId: UUID,
        updateTopicRequest: UpdateTopicRequest
    ): ResponseResult<TopicResponse> {
        return client.patch("/courses/$courseId/topics/$topicId") {
            contentType(ContentType.Application.Json)
            setBody(updateTopicRequest)
        }.toResult()
    }

    override suspend fun getById(courseId: UUID, topicId: UUID): ResponseResult<TopicResponse> {
        return client.get("/courses/$courseId/topics/$topicId").toResult()
    }

    override suspend fun getByCourseId(courseId: UUID): ResponseResult<List<TopicResponse>> {
        return client.get("/courses/$courseId/topics").toResult()
    }

    override suspend fun delete(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements
    ): EmptyResponseResult {
        return client.delete("/courses/$courseId/topics/$topicId") {
            parameter("elements", Json.encodeToString(relatedTopicElements))
        }.toResult()
    }
}