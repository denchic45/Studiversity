package com.denchic45.stuiversity.api.course

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.util.UserId
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
import java.util.UUID

interface CoursesApi {
    suspend fun create(createCourseRequest: CreateCourseRequest): ResponseResult<CourseResponse>

    suspend fun getById(courseId: UUID): ResponseResult<CourseResponse>

    suspend fun getList(
        memberId: UserId? = null,
        studyGroupId: UUID? = null,
        subjectId: UUID? = null,
        archived: Boolean? = false,
        query: String? = null
    ): ResponseResult<List<CourseResponse>>

    suspend fun update(
        courseId: UUID,
        request: UpdateCourseRequest,
    ): ResponseResult<CourseResponse>

    suspend fun getStudyGroups(courseId: UUID): ResponseResult<List<StudyGroupResponse>>

    suspend fun putStudyGroup(courseId: UUID, studyGroupId: UUID): EmptyResponseResult

    suspend fun deleteStudyGroup(courseId: UUID, studyGroupId: UUID): EmptyResponseResult

    suspend fun setArchive(courseId: UUID): EmptyResponseResult

    suspend fun unarchive(courseId: UUID): EmptyResponseResult

    suspend fun search(query: String): ResponseResult<List<CourseResponse>> {
        return getList(query = query, archived = null)
    }

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
        memberId: UserId?,
        studyGroupId: UUID?,
        subjectId: UUID?,
        archived: Boolean?,
        query: String?,
    ): ResponseResult<List<CourseResponse>> {
        return client.get("/courses") {
            parameter("member_id", memberId?.value)
            parameter("study_group_id", studyGroupId)
            parameter("subject_id", subjectId)
            parameter("archived", archived)
            parameter("q", query)
        }.toResult()
    }

    override suspend fun update(
        courseId: UUID,
        request: UpdateCourseRequest,
    ): ResponseResult<CourseResponse> {
        return client.patch("/courses/$courseId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.toResult()
    }

    override suspend fun getStudyGroups(courseId: UUID): ResponseResult<List<StudyGroupResponse>> {
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