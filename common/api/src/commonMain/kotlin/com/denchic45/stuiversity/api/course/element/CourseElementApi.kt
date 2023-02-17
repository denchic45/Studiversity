package com.denchic45.stuiversity.api.course.element

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.SortingCourseElements
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import com.denchic45.stuiversity.util.parametersOf
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CourseElementApi {

    suspend fun update(
        courseId: UUID,
        elementId: UUID,
        updateCourseElementRequest: UpdateCourseElementRequest
    ): ResponseResult<CourseElementResponse>

    suspend fun getByCourseId(
        courseId: UUID,
        sorting: List<SortingCourseElements> = listOf()
    ): ResponseResult<List<CourseElementResponse>>

    suspend fun delete(courseId: UUID, elementId: UUID): EmptyResponseResult
}

class CourseElementApiImpl(private val client: HttpClient) : CourseElementApi {
    override suspend fun update(
        courseId: UUID,
        elementId: UUID,
        updateCourseElementRequest: UpdateCourseElementRequest
    ): ResponseResult<CourseElementResponse> {
        return client.patch("/courses/$courseId/elements/$elementId") {
            contentType(ContentType.Application.Json)
            setBody(updateCourseElementRequest)
        }.toResult()
    }

    override suspend fun getByCourseId(
        courseId: UUID,
        sorting: List<SortingCourseElements>
    ): ResponseResult<List<CourseElementResponse>> {
        return client.get("/courses/$courseId/elements") {
            parametersOf(values = sorting)
        }.toResult()
    }

    override suspend fun delete(courseId: UUID, elementId: UUID): EmptyResponseResult {
        return client.delete("/courses/$courseId/elements/$elementId").toResult()
    }
}