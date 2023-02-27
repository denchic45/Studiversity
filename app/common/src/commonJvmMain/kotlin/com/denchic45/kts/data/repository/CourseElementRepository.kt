package com.denchic45.kts.data.repository

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.domain.model.AttachmentFile
import com.denchic45.kts.data.domain.model.AttachmentLink
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.model.Task
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.topic.CourseTopicsApi
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.github.michaelbull.result.*
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseElementRepository(
    override val networkService: NetworkService,
    private val courseTopicsApi: CourseTopicsApi,
    private val courseElementsApi: CourseElementsApi,
    private val courseWorkApi: CourseWorkApi,
) : NetworkServiceOwner {
    suspend fun findElementsByCourse(
        courseId: UUID,
    ): Resource<Map<TopicResponse?, List<CourseElementResponse>>> = fetchResource {
        val topics = courseTopicsApi.getByCourseId(courseId)
            .onFailure { return@fetchResource Err(it) }
            .unwrap()
        val elements = courseElementsApi.getByCourseId(courseId)
            .onFailure { return@fetchResource Err(it) }
            .unwrap()

        Ok(buildMap {
            put(null, elements.filter { it.topicId == null })
            topics.forEach { topicResponse ->
                put(topicResponse, elements.filter { it.topicId == topicResponse.id })
            }
        })
    }

    suspend fun findById(courseId: UUID, workId: UUID) = fetchResource {
        courseWorkApi.getById(courseId, workId)
    }

    suspend fun addCourseWork(
        courseId: UUID,
        createCourseWorkRequest: CreateCourseWorkRequest,
        attachments: List<Attachment>,
    ) = fetchResource {
        courseWorkApi.create(courseId, createCourseWorkRequest)
            .andThen { element ->
                attachments.map {
                    when (it) {
                        is AttachmentFile -> courseWorkApi.uploadFileToWork(
                            courseId,
                            element.id,
                            it.file
                        )
                        is AttachmentLink -> courseWorkApi.addLinkToWork(
                            courseId,
                            element.id,
                            CreateLinkRequest(it.url)
                        )
                    }
                }.firstOrNull { it is Err } ?: Ok(element)
            }
    }

    suspend fun updateWork(
        courseId: UUID,
        workId: UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest,
    ) = fetchResource {
        courseWorkApi.update(courseId, workId, updateCourseWorkRequest)
    }

    fun findByStudent(
        studentId: UUID? = null,
        late: Boolean? = null,
        statuses: List<SubmissionState>? = null,
    ): Resource<List<CourseElementResponse>> {
        TODO("Not yet implemented")
    }

    suspend fun removeElement(courseId: UUID, elementId: UUID) = fetchResource {
        courseElementsApi.delete(courseId, elementId)
    }
}