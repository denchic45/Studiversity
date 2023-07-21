package com.denchic45.stuiversity.api.course.element

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementsSorting
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import com.denchic45.stuiversity.util.parametersOf
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

interface CourseElementsApi {
    suspend fun update(
        courseId: UUID,
        elementId: UUID,
        request: UpdateCourseElementRequest
    ): ResponseResult<CourseElementResponse>

    suspend fun getById(courseId: UUID, elementId: UUID): ResponseResult<CourseElementResponse>

    suspend fun getByCourseId(
        courseId: UUID,
        sorting: List<CourseElementsSorting> = listOf()
    ): ResponseResult<List<CourseElementResponse>>


    suspend fun delete(courseId: UUID, elementId: UUID): EmptyResponseResult
}

class CourseElementsApiImpl(private val client: HttpClient) : CourseElementsApi {
    override suspend fun update(
        courseId: UUID,
        elementId: UUID,
        request: UpdateCourseElementRequest
    ): ResponseResult<CourseElementResponse> {
        return client.patch("/courses/$courseId/elements/$elementId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getById(
        courseId: UUID,
        elementId: UUID
    ): ResponseResult<CourseElementResponse> {
        return client.get("/courses/$courseId/elements/$elementId").toResult()
    }

    override suspend fun getByCourseId(
        courseId: UUID,
        sorting: List<CourseElementsSorting>
    ): ResponseResult<List<CourseElementResponse>> {
        return client.get("/courses/$courseId/elements") {
            parametersOf(values = sorting)
        }.toResult()
    }

    override suspend fun delete(courseId: UUID, elementId: UUID): EmptyResponseResult {
        return client.delete("/courses/$courseId/elements/$elementId").toResult()
    }
}