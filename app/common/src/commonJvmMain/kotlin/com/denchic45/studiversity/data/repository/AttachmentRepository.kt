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
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toPath
import java.util.*

@Inject
class AttachmentRepository(
    override val networkService: NetworkService,
    private val attachmentLocalDataSource: AttachmentLocalDataSource,
    private val attachmentReferenceLocalDataSource: AttachmentReferenceLocalDataSource,
    private val attachmentStorage: AttachmentStorage,
    private val attachmentApi: AttachmentApi,
    private val database: AppDatabase
) : NetworkServiceOwner {

    fun observeByResource(resource: String, resourceId: UUID): Flow<Resource<List<Attachment2>>> {
        return observeResource(
            query = getAttachmentsByReferenceId(resourceId).distinctUntilChanged(),
            fetch = { attachmentApi.getByResourceId(resource, resourceId) },
            saveFetch = { attachments -> saveAttachments(attachments, resourceId) }
        )
    }

    fun observeByCourseWork(workId: UUID): Flow<Resource<List<Attachment2>>> {
        return observeByResource("course-works", workId)
    }

    fun observeByCourseMaterial(materialId: UUID): Flow<Resource<List<Attachment2>>> {
        return observeByResource("course-materials", materialId)
    }

    fun observeBySubmission(submissionId: UUID): Flow<Resource<List<Attachment2>>> {
        return observeByResource("work-submissions", submissionId)
    }

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
                is FileAttachmentHeader -> Attachment(
                    attachment_id = UUID.randomUUID().toString(),
                    server_id = attachment.id.toString(),
                    attachment_name = attachment.name,
                    url = null,
                    thumbnail_url = null,
                    type = AttachmentType.FILE,
                    path = attachmentStorage.getFilePathByIdAndName(attachment.id).toString(),
                    owner_id = null,
                    sync = false
                )

                is LinkAttachmentHeader -> Attachment(
                    attachment_id = UUID.randomUUID().toString(),
                    server_id = attachment.id.toString(),
                    attachment_name = attachment.name,
                    url = attachment.url,
                    thumbnail_url = attachment.thumbnailUrl,
                    type = AttachmentType.LINK,
                    path = null,
                    owner_id = null,
                    sync = true
                )
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
        resource: String,
        resourceId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> = fetchResource {
        when (request) {
            is CreateFileRequest -> attachmentApi.uploadFile(
                resource,
                resourceId,
                request
            )

            is CreateLinkRequest -> attachmentApi.addLink(
                resource,
                resourceId,
                request
            )

            is UploadedAttachmentRequest -> attachmentApi.addUploadedAttachment(
                resource,
                resourceId,
                request
            )
        }.onSuccess {
            saveAttachment(it, resourceId)
        }
    }

    suspend fun addAttachmentToCourseWork(
        courseWorkId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> = addAttachmentToResource("course-works", courseWorkId, request)

    suspend fun addAttachmentToCourseMaterial(
        courseMaterialId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> = addAttachmentToResource("course-materials", courseMaterialId, request)

    suspend fun addAttachmentToSubmission(
        submissionId: UUID,
        request: AttachmentRequest
    ): Resource<AttachmentHeader> = addAttachmentToResource("work-submissions", submissionId, request)

    private suspend fun removeAttachmentLocally(attachmentId: UUID, referenceId: UUID) {
        attachmentReferenceLocalDataSource.delete(
            attachmentId.toString(),
            referenceId.toString()
        )
        attachmentStorage.delete(attachmentId)
    }

    suspend fun removeFromResource(resource: String, resourceId: UUID, attachmentId: UUID): EmptyResource {
        return fetchResource {
            attachmentApi.delete(resource, resourceId, attachmentId).onSuccess {
                attachmentStorage.delete(attachmentId)
            }
        }
    }

    suspend fun removeFromCourseWork(courseWorkId: UUID, attachmentId: UUID): EmptyResource {
        return removeFromResource("course-works", courseWorkId, attachmentId)
    }

    suspend fun removeFromCourseMaterial(courseMaterialId: UUID, attachmentId: UUID): EmptyResource {
        return removeFromResource("course-materials", courseMaterialId, attachmentId)
    }

    suspend fun removeFromSubmission(submissionId: UUID, attachmentId: UUID): EmptyResource {
        return removeFromResource("work-submissions", submissionId, attachmentId)
    }
}