package com.denchic45.kts.data.repository

import com.denchic45.kts.AttachmentEntity
import com.denchic45.kts.data.db.local.source.AttachmentLocalDataSource
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.data.observeResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.AttachmentStorage
import com.denchic45.stuiversity.api.course.element.model.AttachmentType
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentHeader
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toPath
import java.util.*

@Inject
class AttachmentRepository(
    override val networkService: NetworkService,
    private val attachmentLocalDataSource: AttachmentLocalDataSource,
    private val attachmentStorage: AttachmentStorage,
    private val submissionsApi: SubmissionsApi,
) : NetworkServiceOwner {

    fun observeBySubmission(
        courseId: UUID,
        workId: UUID,
        submissionId: UUID,
    ) = observeResource(
        query = attachmentLocalDataSource.getByDirPath(attachmentStorage.path.toString()).map {
            it.map { entity ->
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
        },
        fetch = { submissionsApi.getAttachments(courseId, workId, submissionId) },
        saveFetch = {
            it.forEach { attachment ->
                attachmentLocalDataSource.upsert(
                    when (attachment) {
                        is FileAttachmentHeader -> {
                            val fileName = attachment.fileItem.name
                            AttachmentEntity(
                                attachment_id = attachment.id.toString(),
                                attachment_name = fileName,
                                url = null,
                                thumbnail_url = null,
                                type = AttachmentType.FILE,
                                path = courseWorkStorage.getFilePath(fileName).toString(),
                                owner_id = null,
                                sync = false
                            )
                        }
                        is LinkAttachmentHeader -> {
                            val linkAttachmentResponse = attachment.linkAttachmentResponse
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
        }
    )
}