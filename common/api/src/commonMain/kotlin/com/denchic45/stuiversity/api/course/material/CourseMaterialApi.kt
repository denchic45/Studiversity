package com.denchic45.stuiversity.api.course.material

import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import com.denchic45.stuiversity.util.UUIDWrapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CourseMaterialApi {
    suspend fun create(
        courseId: UUID,
        request: CreateCourseMaterialRequest
    ): ResponseResult<CourseMaterialResponse>

    suspend fun update(
        materialId: UUID,
        request: UpdateCourseMaterialRequest
    ): ResponseResult<CourseMaterialResponse>

    suspend fun getById(materialId: UUID): ResponseResult<CourseMaterialResponse>

    suspend fun getByAuthor(
        authorId: UUIDWrapper,
        late: Boolean? = null,
        submitted: Boolean? = null
    ): ResponseResult<List<CourseMaterialResponse>>
}

class CourseMaterialApiImpl(private val client: HttpClient) : CourseMaterialApi {
    override suspend fun create(
        courseId: UUID,
        request: CreateCourseMaterialRequest
    ): ResponseResult<CourseMaterialResponse> {
        return client.post("/courses/$courseId/materials") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun update(
        materialId: UUID,
        request: UpdateCourseMaterialRequest,
    ): ResponseResult<CourseMaterialResponse> {
        return client.patch("/course-materials/$materialId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getById(
        materialId: UUID
    ): ResponseResult<CourseMaterialResponse> {
        return client.get("/course-materials/$materialId").toResult()
    }

    override suspend fun getByAuthor(
        authorId: UUIDWrapper,
        late: Boolean?,
        submitted: Boolean?
    ): ResponseResult<List<CourseMaterialResponse>> {
        return client.get("/course-works") {
            parameter("late", late)
            parameter("author_id", authorId.value)
            parameter("submitted", submitted)
        }.toResult()
    }
}