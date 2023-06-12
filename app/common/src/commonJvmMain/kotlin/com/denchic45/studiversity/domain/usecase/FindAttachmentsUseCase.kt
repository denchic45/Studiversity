package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.domain.model.Attachment2
import com.denchic45.studiversity.data.domain.model.FileAttachment2
import com.denchic45.studiversity.data.domain.model.FileState
import com.denchic45.studiversity.data.domain.model.LinkAttachment2
import com.denchic45.studiversity.data.service.DownloadsService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.flatMapResourceFlow
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.UUID

abstract class FindAttachmentsUseCase constructor(
    private val downloadService: DownloadsService,
) {
    private val observableDownloads: MutableMap<UUID, Flow<FileState>> = mutableMapOf()

    protected fun observeAttachments(
        flow: Flow<Resource<List<Attachment2>>>,
        ownerId:UUID
    ): Flow<Resource<List<Attachment2>>> {
        println("OBSERVE_ATTACHMENTS: $flow")
        return flow.onEach {
            it.onSuccess {
                println("ONEACH:")
                it.forEach { println("\t $it") }
            }
        }.flatMapResourceFlow { attachments ->
            observeDownloadsFlow(attachments,ownerId).map { states ->
                getAttachmentResource(attachments, states)
            }
        }
    }

    private fun getAttachmentResource(
        attachments: List<Attachment2>,
        states: Map<UUID, FileState>
    ) = resourceOf(
        attachments.map { attachment ->
            when (attachment) {
                is FileAttachment2 -> if (attachment.state == FileState.Preview)
                    attachment.copy(state = states.getValue(attachment.id))
                else attachment

                is LinkAttachment2 -> attachment
            }
        }
    )

    private fun observeDownloadsFlow(attachments: List<Attachment2>, ownerId: UUID): Flow<Map<UUID, FileState>> {
        println("DOWNLOAD_FILE observeDownloadsFlow: $ownerId $attachments")
        val allFileAttachments = attachments.filterIsInstance<FileAttachment2>()
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