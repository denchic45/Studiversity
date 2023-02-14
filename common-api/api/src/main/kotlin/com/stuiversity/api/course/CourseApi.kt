package com.stuiversity.api.course

import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import com.stuiversity.api.course.model.CourseResponse
import com.stuiversity.api.course.model.CreateCourseRequest
import com.stuiversity.api.course.model.UpdateCourseRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CoursesApi {
    suspend fun create(createCourseRequest: CreateCourseRequest): ResponseResult<CourseResponse>

    suspend fun getById(courseId: UUID): ResponseResult<CourseResponse>

    suspend fun update(courseId: UUID, updateCourseRequest: UpdateCourseRequest): ResponseResult<CourseResponse>

    suspend fun setArchive(courseId: UUID): EmptyResponseResult

    suspend fun unarchive(courseId: UUID): EmptyResponseResult

    suspend fun search(query: String): ResponseResult<List<CourseResponse>>

    suspend fun delete(courseId: UUID): EmptyResponseResult
}

class CourseApiImpl(private val client: HttpClient) : CoursesApi {
    override suspend fun create(createCourseRequest: CreateCourseRequest): ResponseResult<CourseResponse> {
        return client.post("/courses") {
            contentType(ContentType.Application.Json)
            setBody(createCourseRequest)
        }.toResult()
    }

    override suspend fun getById(courseId: UUID): ResponseResult<CourseResponse> {
        return client.get("/courses/$courseId").toResult()
    }

    override suspend fun update(
        courseId: UUID,
        updateCourseRequest: UpdateCourseRequest
    ): ResponseResult<CourseResponse> {
        return client.patch("/courses/$courseId") {
            contentType(ContentType.Application.Json)
            setBody(updateCourseRequest)
        }.toResult()
    }

    override suspend fun setArchive(courseId: UUID): EmptyResponseResult {
        return client.put("/courses/${courseId}/archived").toResult()
    }

    override suspend fun unarchive(courseId: UUID): EmptyResponseResult {
        return client.delete("/courses/${courseId}/archived").toResult()
    }

    override suspend fun search(query: String): ResponseResult<List<CourseResponse>> {
        return client.get("/courses") {
            parameter("q", query)
        }.toResult()
    }

    override suspend fun delete(courseId: UUID): EmptyResponseResult {
        return client.delete("/courses/$courseId").toResult()
    }
}