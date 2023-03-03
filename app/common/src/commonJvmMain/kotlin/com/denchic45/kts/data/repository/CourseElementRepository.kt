package com.denchic45.kts.data.repository

import com.denchic45.kts.AttachmentEntity
import com.denchic45.kts.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.observeResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.CourseWorkStorage
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.topic.CourseTopicsApi
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.github.michaelbull.result.*
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseElementRepository(
    override val networkService: NetworkService,
    private val attachmentLocalDataSource: AttachmentLocalDataSource,
    private val courseWorkStorage: CourseWorkStorage,
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

    suspend fun findWorkById(courseId: UUID, workId: UUID) = fetchResource {
        courseWorkApi.getById(courseId, workId)
    }

    suspend fun addCourseWork(
        courseId: UUID,
        createCourseWorkRequest: CreateCourseWorkRequest,
    ) = fetchResource {
        courseWorkApi.create(courseId, createCourseWorkRequest)
    }

    suspend fun addAttachmentToWork(
        courseId: UUID,
        workId: UUID,
        attachment: AttachmentRequest
    ): Resource<AttachmentHeader> = fetchResource {
        when (attachment) {
            is CreateFileRequest -> courseWorkApi.uploadFileToWork(
                courseId,
                workId,
                attachment
            )
            is CreateLinkRequest -> courseWorkApi.addLinkToWork(
                courseId,
                workId,
                attachment
            )
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

    suspend fun findAttachments(courseId: UUID, workId: UUID) = observeResource(
        query = attachmentLocalDataSource.getByDirPath(courseWorkStorage.path.toString()),
        fetch = { courseWorkApi.getAttachments(courseId, workId) },
        saveFetch = {
            it.forEach { attachment ->
                (attachment as? FileAttachmentHeader)?.let { fileAttachment ->
                    val fileName = fileAttachment.fileItem.name
                    attachmentLocalDataSource.upsert(
                        AttachmentEntity(
                            attachment_id = fileAttachment.id.toString(),
                            attachment_name = fileName,
                            file_path = courseWorkStorage.getFilePath(fileName).toString(),
                            sync = false
                        )
                    )
                }
            }
        }
    )

    suspend fun removeElement(courseId: UUID, elementId: UUID) = fetchResource {
        courseElementsApi.delete(courseId, elementId)
    }
}