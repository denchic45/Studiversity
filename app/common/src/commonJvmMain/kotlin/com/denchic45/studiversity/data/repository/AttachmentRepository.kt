package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.studiversity.data.db.local.source.AttachmentReferenceLocalDataSource
import com.denchic45.studiversity.data.db.local.suspendedTransaction
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.observeResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.data.storage.AttachmentStorage
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.model.FileAttachment2
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.model.LinkAttachment2
import com.denchic45.studiversity.domain.resource.EmptyResource
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.entity.AppDatabase
import com.denchic45.studiversity.entity.Attachment
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.material.CourseMaterialApi
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toPath
import java.util.*

@Inject
class AttachmentRepository(
    override val networkService: NetworkService,
    private val attachmentLocalDataSource: AttachmentLocalDataSource,
    private val attachmentReferenceLocalDataSource: AttachmentReferenceLocalDataSource,
    private val attachmentStorage: AttachmentStorage,
    private val submissionsApi: SubmissionsApi,
    private val courseWorkApi: CourseWorkApi,
    private val courseMaterialApi: CourseMaterialApi,
    private val attachmentApi: AttachmentApi,
    private val database: AppDatabase
) : NetworkServiceOwner {

    fun observeByResource(
        resourceType: String,
        resourceId: UUID,
    ): Flow<Resource<List<Attachment2>>> = observeResource(
        query = getAttachmentsByReferenceId(resourceId).distinctUntilChanged(),
        fetch = { attachmentApi.getByResourceId(resourceType, resourceId) },
        saveFetch = { attachments -> saveAttachments(attachments, resourceId) }
    )

    @Deprecated(message = "")
    fun observeBySubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
    ): Flow<Resource<List<Attachment2>>> = observeResource(
        query = getAttachmentsByReferenceId(submissionId).distinctUntilChanged(),
        fetch = { submissionsApi.getAttachments(courseId, workId, submissionId) },
        saveFetch = { attachments -> saveAttachments(attachments, submissionId) }
    )

    @Deprecated(message = "")
    fun observeByCourseWork(
        courseId: UUID,
        workId: UUID,
    ): Flow<Resource<List<Attachment2>>> = observeResource(
        query = getAttachmentsByReferenceId(workId).distinctUntilChanged().onEach { it ->
            println("GET_ATTACHMENTS_BY: $workId")
            it.forEach {
                println("\t${it}")
            }
        },
        fetch = { courseWorkApi.getAttachments(courseId, workId) },
        saveFetch = { attachments -> saveAttachments(attachments, workId) }
    )

    @Deprecated(message = "")
    fun observeByCourseMaterial(
        courseId: UUID,
        materialId: UUID,
    ): Flow<Resource<List<Attachment2>>> = observeResource(
        query = getAttachmentsByReferenceId(materialId).distinctUntilChanged(),
        fetch = { courseWorkApi.getAttachments(courseId, materialId) },
        saveFetch = { attachments -> saveAttachments(attachments, materialId) }
    )

    private fun getAttachmentsByReferenceId(referenceId: UUID): Flow<List<Attachment2>> {
        return attachmentLocalDataSource.getByReferenceId(referenceId.toString())
            .map { attachmentEntities ->
                attachmentEntities.map { entity ->
                    val id = entity.attachment_id.toUUID()
                    when (entity.type) {
                        AttachmentType.FILE -> FileAttachment2(
                            id = id,
                            name = entity.attachment_name,
                            path = entity.path!!.toPath(),
                            state = if (entity.sync) FileState.Downloaded else FileState.Preview
                        )

                        AttachmentType.LINK -> {
                            LinkAttachment2(id, entity.url!!)
                        }
                    }
                }
            }
    }

    private suspend fun saveAttachments(attachments: List<AttachmentHeader>, referenceId: UUID) {
        database.suspendedTransaction {
            deleteNotContainsAttachments(attachments, referenceId)
            attachments.forEach { attachment ->
                saveAttachment(attachment, referenceId)
            }
        }
    }

    private suspend fun saveAttachment(attachment: AttachmentHeader, referenceId: UUID?) {
        attachmentLocalDataSource.upsert(
            when (attachment) {
                is FileAttachmentHeader -> {
                    val fileName = attachment.item.name
                    Attachment(
                        attachment_id = attachment.id.toString(),
                        attachment_name = fileName,
                        url = null,
                        thumbnail_url = null,
                        type = AttachmentType.FILE,
                        path = attachmentStorage.getFilePathByIdAndName(attachment.id, fileName)
                            .toString(),
                        owner_id = null,
                        sync = false
                    )
                }

                is LinkAttachmentHeader -> {
                    val linkAttachmentResponse = attachment.item
                    Attachment(
                        attachment_id = attachment.id.toString(),
                        attachment_name = linkAttachmentResponse.name,
                        url = linkAttachmentResponse.url,
                        thumbnail_url = linkAttachmentResponse.thumbnailUrl,
                        type = AttachmentType.LINK,
                        path = null,
                        owner_id = null,
                        sync = true
                    )
                }
            },
            referenceId?.toString()
        )
    }

    private suspend fun deleteNotContainsAttachments(
        attachments: List<AttachmentHeader>,
        referenceId: UUID
    ) {
        val attachmentIds = attachments.map { attachment -> attachment.id.toString() }
        attachmentReferenceLocalDataSource.deleteByNotInIds(attachmentIds, referenceId.toString())

        val unreferences = attachmentLocalDataSource.getUnreferenced()
        unreferences.forEach { unreferencedAttachmentId ->
            attachmentLocalDataSource.delete(unreferencedAttachmentId)
            attachmentStorage.delete(unreferencedAttachmentId.toUUID())
        }
    }

    suspend fun addAttachmentToResource(
        resourceType: String,
        resourceId: UUID,
        attachmentRequest: AttachmentRequest
    ): Resource<AttachmentHeader> = fetchResource {
        when (attachmentRequest) {
            is CreateFileRequest -> attachmentApi.uploadFile(
                resourceType,
                resourceId,
                attachmentRequest
            )

            is CreateLinkRequest -> attachmentApi.addLink(
                resourceType,
                resourceId,
                attachmentRequest
            )
        }.onSuccess {
            saveAttachment(it, resourceId)
        }
    }

    suspend fun addAttachmentToSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        attachmentRequest: AttachmentRequest
    ): Resource<AttachmentHeader> = fetchResource {
        when (attachmentRequest) {
            is CreateFileRequest -> submissionsApi.uploadFile(
                courseId,
                workId,
                submissionId,
                attachmentRequest
            )

            is CreateLinkRequest -> submissionsApi.addLink(
                courseId,
                workId,
                submissionId,
                attachmentRequest
            )
        }.onSuccess {
            saveAttachment(it, submissionId)
        }
    }

    suspend fun addAttachmentToMaterial(
        courseId: UUID,
        materialId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> = fetchResource {
        when (request) {
            is CreateFileRequest -> courseMaterialApi.uploadFile(
                courseId,
                materialId,
                request
            )

            is CreateLinkRequest -> courseMaterialApi.addLink(
                courseId,
                materialId,
                request
            )
        }.onSuccess {
            saveAttachment(it, materialId)
        }
    }

    suspend fun removeFromCourseWork(
        attachmentId: UUID,
        courseId: UUID,
        workId: UUID
    ): EmptyResource = fetchResource {
        courseWorkApi.deleteAttachment(courseId, workId, attachmentId)
            .onSuccess { removeAttachmentLocally(attachmentId, workId) }
    }

    suspend fun removeFromSubmission(
        attachmentId: UUID,
        courseId: UUID,
        workId: UUID,
        submissionId: UUID
    ): EmptyResource = fetchResource {
        submissionsApi.deleteAttachment(courseId, workId, submissionId, attachmentId)
            .onSuccess { removeAttachmentLocally(attachmentId, workId) }
    }

    suspend fun removeFromCourseMaterial(
        attachmentId: UUID,
        courseId: UUID,
        materialId: UUID
    ): EmptyResource = fetchResource {
        courseMaterialApi.deleteAttachment(courseId, materialId, attachmentId)
            .onSuccess { removeAttachmentLocally(attachmentId, materialId) }
    }

    private suspend fun removeAttachmentLocally(attachmentId: UUID, referenceId: UUID) {
        attachmentReferenceLocalDataSource.delete(
            attachmentId.toString(),
            referenceId.toString()
        )
        attachmentStorage.delete(attachmentId)
    }

    suspend fun removeById(attachmentId: UUID): EmptyResource = fetchResource {
        attachmentApi.delete(attachmentId).onSuccess {
            attachmentStorage.delete(attachmentId)
        }
    }

//    suspend fun removeAttachmentByReferenceId(
//        courseId: UUID,
//        workId: UUID, attachmentId: UUID,
//    ) = fetchResource {
//
//        courseWorkApi.deleteAttachmentFromWork(courseId, workId, attachmentId)
//    }
}