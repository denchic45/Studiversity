package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.material.CourseMaterialApi
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.topic.CourseTopicApi
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.util.uuidOfMe
import com.github.michaelbull.result.coroutines.binding.binding
import com.github.michaelbull.result.map
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseElementRepository @javax.inject.Inject constructor(
    override val networkService: NetworkService,
    private val courseTopicApi: CourseTopicApi,
    private val courseElementsApi: CourseElementsApi,
    private val courseWorkApi: CourseWorkApi,
    private val courseMaterialApi: CourseMaterialApi
) : NetworkServiceOwner {
    suspend fun findElementsByCourse(courseId: UUID) = fetchResource {
        binding {
            val topics = async { courseTopicApi.getByCourseId(courseId).bind() }
            val elements = async { courseElementsApi.getByCourseId(courseId).bind() }
            topics.await() to elements.await()
        }.map { (topics, elements) ->
            buildList {
                add(null to elements.filter { it.topicId == null })
                topics.forEach { topicResponse ->
                    add(topicResponse to elements.filter { it.topicId == topicResponse.id })
                }
            }
        }
    }

    suspend fun findWorkById(courseId: UUID, workId: UUID) = fetchResource {
        courseWorkApi.getById(courseId, workId)
    }

    suspend fun findMaterialById(courseId: UUID, materialId: UUID) = fetchResource {
        courseMaterialApi.getById(courseId, materialId)
    }

    suspend fun addCourseWork(
        courseId: UUID,
        createCourseWorkRequest: CreateCourseWorkRequest,
    ) = fetchResource {
        courseWorkApi.create(courseId, createCourseWorkRequest)
    }

    suspend fun addCourseMaterial(
        courseId: UUID,
        request: CreateCourseMaterialRequest,
    ) = fetchResource {
        courseMaterialApi.create(courseId, request)
    }

//    suspend fun addLinkToWork(
//        courseId: UUID,
//        workId: UUID,
//        linkUrl: String
//    ) = fetchResource {
//        courseWorkApi.addLinkToWork(
//            courseId = courseId,
//            courseWorkId = workId,
//            createLinkRequest = CreateLinkRequest(linkUrl)
//        )
//    }

//    suspend fun addFileToWork(
//        courseId: UUID,
//        workId: UUID,
//        uri: Uri
//    ) = fetchResource {
//        courseWorkApi.uploadFileToWork(
//            courseId = courseId,
//            courseWorkId = workId,
//            createFileRequest = CreateFileRequest(
//                fileProvider.getName(uri),
//                fileProvider.getBytes(uri)
//            )
//        )
//    }

    suspend fun addAttachmentToWork(
        courseId: UUID,
        workId: UUID,
        attachment: AttachmentRequest,
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

    suspend fun updateCourseWork(
        courseId: UUID,
        workId: UUID,
        updateCourseWorkRequest: UpdateCourseWorkRequest,
    ) = fetchResource {
        courseWorkApi.update(courseId, workId, updateCourseWorkRequest)
    }

    suspend fun updateCourseMaterial(
        courseId: UUID,
        materialId: UUID,
        request: UpdateCourseMaterialRequest,
    ) = fetchResource {
        courseMaterialApi.update(courseId, materialId, request)
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

//    fun findAttachments(courseId: UUID, workId: UUID) = observeResource(
//        query = attachmentLocalDataSource.getByDirPath(courseWorkStorage.path.toString()).map {
//            it.map { entity ->
//                val id = entity.attachment_id.toUUID()
//                when (entity.type) {
//                    AttachmentType.FILE -> FileAttachment2(
//                        id = id,
//                        path = entity.path!!.toPath(),
//                        state = if (entity.sync) FileState.Downloaded else FileState.Preview
//                    )
//                    AttachmentType.LINK -> {
//                        LinkAttachment2(id, entity.url!!)
//                    }
//                }
//            }
//        },
//        fetch = { courseWorkApi.getAttachments(courseId, workId) },
//        saveFetch = {
//            it.forEach { attachment ->
//                attachmentLocalDataSource.upsert(
//                    when (attachment) {
//                        is FileAttachmentHeader -> {
//                            val fileName = attachment.fileItem.name
//                            AttachmentEntity(
//                                attachment_id = attachment.id.toString(),
//                                attachment_name = fileName,
//                                url = null,
//                                thumbnail_url = null,
//                                type = AttachmentType.FILE,
//                                path = courseWorkStorage.getFilePath(fileName).toString(),
//                                owner_id = null,
//                                sync = false
//                            )
//                        }
//                        is LinkAttachmentHeader -> {
//                            val linkAttachmentResponse = attachment.linkAttachmentResponse
//                            AttachmentEntity(
//                                attachment_id = attachment.id.toString(),
//                                attachment_name = linkAttachmentResponse.name,
//                                url = linkAttachmentResponse.url,
//                                thumbnail_url = linkAttachmentResponse.thumbnailUrl,
//                                type = AttachmentType.FILE,
//                                path = null,
//                                owner_id = null,
//                                sync = true
//                            )
//                        }
//                    }
//                )
//            }
//        }
//    )

    suspend fun removeElement(courseId: UUID, elementId: UUID) = fetchResource {
        courseElementsApi.delete(courseId, elementId)
    }
}