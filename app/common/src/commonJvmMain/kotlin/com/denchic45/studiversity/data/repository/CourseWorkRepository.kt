package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.util.uuidOfMe
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseWorkRepository(
    override val networkService: NetworkService,
    private val courseWorkApi: CourseWorkApi,
) : NetworkServiceOwner {

    suspend fun add(
        courseId: UUID,
        request: CreateCourseWorkRequest,
    ) = fetchResource {
        courseWorkApi.create(courseId, request)
    }

    suspend fun update(
        workId: UUID,
        request: UpdateCourseWorkRequest
    ) = fetchResource {
        courseWorkApi.update(workId, request)
    }

    suspend fun findById(workId: UUID) = fetchResource {
        courseWorkApi.getById(workId)
    }

    fun findUpcomingByYourAuthor(): Flow<Resource<List<CourseWorkResponse>>> = fetchResourceFlow {
        courseWorkApi.getByAuthor(authorId = uuidOfMe(), late = false, submitted = false)
    }

    fun findOverdueByYourAuthor(): Flow<Resource<List<CourseWorkResponse>>> = fetchResourceFlow {
        courseWorkApi.getByAuthor(authorId = uuidOfMe(), late = true, submitted = false)
    }

    fun findSubmittedByYourAuthor(): Flow<Resource<List<CourseWorkResponse>>> = fetchResourceFlow {
        courseWorkApi.getByAuthor(authorId = uuidOfMe(), submitted = true)
    }
}