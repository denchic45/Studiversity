package com.denchic45.stuiversity.api.course

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CoursesApi {
    suspend fun create(createCourseRequest: CreateCourseRequest): ResponseResult<CourseResponse>

    suspend fun getById(courseId: UUID): ResponseResult<CourseResponse>

    suspend fun getList(
        memberId: UUID? = null,
        studyGroupId: UUID? = null,
        subjectId: UUID? = null,
        query: String? = null
    ): ResponseResult<List<CourseResponse>>

    suspend fun getCoursesByMe(): ResponseResult<CourseResponse>

    suspend fun update(
        courseId: UUID,
        updateCourseRequest: UpdateCourseRequest,
    ): ResponseResult<CourseResponse>

    suspend fun getStudyGroupIds(courseId: UUID): ResponseResult<List<UUID>>

    suspend fun putStudyGroup(courseId: UUID, studyGroupId: UUID): EmptyResponseResult

    suspend fun deleteStudyGroup(courseId: UUID, studyGroupId: UUID): EmptyResponseResult

    suspend fun setArchive(courseId: UUID): EmptyResponseResult

    suspend fun unarchive(courseId: UUID): EmptyResponseResult

    suspend fun search(query: String): ResponseResult<List<CourseResponse>> = getList(query = query)

    suspend fun delete(courseId: UUID): EmptyResponseResult

//    suspend fun getBySubjectNameAndStudyGroupId(subjectName: String, studyGroupId: UUID):ResponseResult<CourseResponse>
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

    override suspend fun getList(
        memberId: UUID?,
        studyGroupId: UUID?,
        subjectId: UUID?,
        query: String?,
    ): ResponseResult<List<CourseResponse>> {
        return client.get("/courses") {
            parameter("member_id", memberId)
            parameter("study_group_id", studyGroupId)
            parameter("subject_id", subjectId)
            parameter("q", query)
        }.toResult()
    }

    override suspend fun getCoursesByMe(): ResponseResult<CourseResponse> {
        return client.get("/me/courses").toResult()
    }

    override suspend fun update(
        courseId: UUID,
        updateCourseRequest: UpdateCourseRequest,
    ): ResponseResult<CourseResponse> {
        return client.patch("/courses/$courseId") {
            contentType(ContentType.Application.Json)
            setBody(updateCourseRequest)
        }.toResult()
    }

    override suspend fun getStudyGroupIds(courseId: UUID): ResponseResult<List<UUID>> {
        return client.get("/courses/$courseId/studygroups").toResult()
    }

    override suspend fun putStudyGroup(courseId: UUID, studyGroupId: UUID): EmptyResponseResult {
        return client.put("/courses/$courseId/studygroups/$studyGroupId").toResult()
    }

    override suspend fun deleteStudyGroup(courseId: UUID, studyGroupId: UUID): EmptyResponseResult {
        return client.delete("/courses/$courseId/studygroups/$studyGroupId").toResult()
    }

    override suspend fun setArchive(courseId: UUID): EmptyResponseResult {
        return client.put("/courses/${courseId}/archived").toResult()
    }

    override suspend fun unarchive(courseId: UUID): EmptyResponseResult {
        return client.delete("/courses/${courseId}/archived").toResult()
    }

    override suspend fun delete(courseId: UUID): EmptyResponseResult {
        return client.delete("/courses/$courseId").toResult()
    }

//    override suspend fun getBySubjectNameAndStudyGroupId(subjectName: String, studyGroupId: UUID): ResponseResult<CourseResponse> {
//        return client.get("/courses") {
//            parameter("subjectName", subjectName)
//            parameter("studyGroupId", studyGroupId)
//        }.toResult()
//    }
}