package com.denchic45.kts.data.repository

import com.denchic45.kts.AttachmentEntity
import com.denchic45.kts.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.kts.data.db.local.source.AttachmentReferenceLocalDataSource
import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.observeResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.AttachmentStorage
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.element.model.AttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import com.denchic45.stuiversity.api.course.element.model.AttachmentType
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentHeader
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toPath
import java.util.UUID

@Inject
class AttachmentRepository @javax.inject.Inject constructor(
    override val networkService: NetworkService,
    private val attachmentLocalDataSource: AttachmentLocalDataSource,
    private val attachmentReferenceLocalDataSource: AttachmentReferenceLocalDataSource,
    private val attachmentStorage: AttachmentStorage,
    private val submissionsApi: SubmissionsApi,
    private val courseWorkApi: CourseWorkApi
) : NetworkServiceOwner {

    fun observeBySubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
    ): Flow<Resource<List<Attachment2>>> = observeResource(
        query = getAttachmentsByReferenceId(submissionId),
        fetch = { submissionsApi.getAttachments(courseId, workId, submissionId) },
        saveFetch = { attachments -> saveAttachments(attachments, submissionId) }
    )

    fun observeByCourseWork(
        courseId: UUID,
        workId: UUID,
    ): Flow<Resource<List<Attachment2>>> = observeResource(
        query = getAttachmentsByReferenceId(workId),
        fetch = { courseWorkApi.getAttachments(courseId, workId) },
        saveFetch = { attachments -> saveAttachments(attachments, workId) }
    )

    private fun getAttachmentsByReferenceId(referenceId: UUID): Flow<List<Attachment2>> {
        return attachmentLocalDataSource.getByReferenceId(referenceId.toString())
            .map { attachmentEntities ->
                attachmentEntities.map { entity ->
                    val id = entity.attachment_id.toUUID()
                    when (entity.type) {
                        AttachmentType.FILE -> FileAttachment2(
                            id = id,
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
        deleteNotContainsAttachments(attachments, referenceId)
        attachments.forEach { attachment ->
            saveAttachment(attachment)
        }
    }

    private suspend fun saveAttachment(attachment: AttachmentHeader) {
        attachmentLocalDataSource.upsert(
            when (attachment) {
                is FileAttachmentHeader -> {
                    val fileName = attachment.item.name
                    AttachmentEntity(
                        attachment_id = attachment.id.toString(),
                        attachment_name = fileName,
                        url = null,
                        thumbnail_url = null,
                        type = AttachmentType.FILE,
                        path = attachmentStorage.getFilePath(attachment.id).toString(),
                        owner_id = null,
                        sync = false
                    )
                }

                is LinkAttachmentHeader -> {
                    val linkAttachmentResponse = attachment.item
                    AttachmentEntity(
                        attachment_id = attachment.id.toString(),
                        attachment_name = linkAttachmentResponse.name,
                        url = linkAttachmentResponse.url,
                        thumbnail_url = linkAttachmentResponse.thumbnailUrl,
                        type = AttachmentType.FILE,
                        path = null,
                        owner_id = null,
                        sync = true
                    )
                }
            }
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

    suspend fun addAttachmentToSubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
        attachmentRequest: AttachmentRequest
    ): Resource<AttachmentHeader> = fetchResource {
        when (attachmentRequest) {
            is CreateFileRequest -> {
                submissionsApi.uploadFileToSubmission(
                    courseId,
                    workId,
                    submissionId,
                    attachmentRequest
                )
            }

            is CreateLinkRequest -> submissionsApi.addLinkToSubmission(
                courseId,
                workId,
                submissionId,
                attachmentRequest
            )
        }.onSuccess {
            saveAttachment(it)
        }
    }
}