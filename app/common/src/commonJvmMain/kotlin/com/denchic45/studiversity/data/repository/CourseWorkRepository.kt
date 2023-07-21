package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.util.uuidOfMe
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

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
        courseId: UUID,
        workId: UUID,
        request: UpdateCourseWorkRequest,
    ) = fetchResource {
        courseWorkApi.update(courseId, workId, request)
    }

    suspend fun findById(courseId: UUID, workId: UUID) = fetchResource {
        courseWorkApi.getById(courseId, workId)
    }

    suspend fun addAttachment(
        courseId: UUID,
        workId: UUID,
        request: AttachmentRequest,
    ): Resource<AttachmentHeader> = fetchResource {
        when (request) {
            is CreateFileRequest -> courseWorkApi.uploadFileToWork(
                courseId,
                workId,
                request
            )

            is CreateLinkRequest -> courseWorkApi.addLinkToWork(
                courseId,
                workId,
                request
            )
        }
    }

    fun findUpcomingByYourAuthor(
    ): Flow<Resource<List<CourseWorkResponse>>> = fetchResourceFlow {
        courseWorkApi.getByAuthor(authorId = uuidOfMe(), late = false, submitted = false)
    }

    fun findOverdueByYourAuthor(
    ): Flow<Resource<List<CourseWorkResponse>>> = fetchResourceFlow {
        courseWorkApi.getByAuthor(authorId = uuidOfMe(), late = true, submitted = false)
    }

    fun findSubmittedByYourAuthor(
    ): Flow<Resource<List<CourseWorkResponse>>> = fetchResourceFlow {
        courseWorkApi.getByAuthor(authorId = uuidOfMe(), submitted = true)
    }
}