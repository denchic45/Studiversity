package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.flatMapResourceFlow
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.FileState
import kotlinx.coroutines.flow.*
import java.util.*

abstract class FindAttachmentsUseCase(
    private val downloadService: DownloadsService,
) {
    private val observableDownloads: MutableMap<UUID, Flow<FileState>> = mutableMapOf()

    protected fun observeAttachments(
        flow: Flow<Resource<List<AttachmentItem>>>,
        ownerId: UUID
    ): Flow<Resource<List<AttachmentItem>>> {
        println("OBSERVE_ATTACHMENTS: $flow")
        return flow.onEach {
            it.onSuccess {
                println("ONEACH:")
                it.forEach { println("\t $it") }
            }
        }.flatMapResourceFlow { attachments ->
            observeDownloadsFlow(attachments, ownerId).map { states ->
                getAttachmentResource(attachments, states)
            }
        }
    }

    private fun getAttachmentResource(
        attachments: List<AttachmentItem>,
        states: Map<UUID, FileState>
    ): Resource.Success<List<AttachmentItem>> = resourceOf(
        attachments.map { attachment ->
            when (attachment) {
                is AttachmentItem.FileAttachmentItem -> if (attachment.state == FileState.Preview)
                    attachment.copy(state = states.getValue(attachment.id))
                else attachment

                is AttachmentItem.LinkAttachmentItem -> attachment
            }
        }
    )

    private fun observeDownloadsFlow(attachments: List<AttachmentItem>, ownerId: UUID): Flow<Map<UUID, FileState>> {
        println("DOWNLOAD_FILE observeDownloadsFlow: $ownerId $attachments")
        val allFileAttachments = attachments.filterIsInstance<AttachmentItem.FileAttachmentItem>()
        val previewFileAttachments = allFileAttachments
            .filter { it.state == FileState.Preview }

        // Remove missing downloads
        observableDownloads.keys.forEach { downloadingAttachmentId ->
            if (allFileAttachments.none { it.id == downloadingAttachmentId }) {
                downloadService.cancel(downloadingAttachmentId)
                observableDownloads.remove(downloadingAttachmentId)
                println("DOWNLOAD_FILE cancel file: $downloadingAttachmentId")
            }
        }

        // Add observable downloads
        previewFileAttachments.forEach { fileAttachment ->
            val attachmentId = fileAttachment.id
            observableDownloads.putIfAbsent(
                attachmentId,
                downloadService.getDownloading(attachmentId)
            )
        }
        return if (observableDownloads.values.isEmpty()) {
            flowOf(emptyMap())
        } else {
            combine(observableDownloads.values) { states ->
                previewFileAttachments.zip(states) { unloadedFileAttachment, state ->
                    unloadedFileAttachment.id to state
                }.toMap()
            }
        }
    }
}