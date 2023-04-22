package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.data.service.DownloadsService
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResourceFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.*

abstract class FindAttachmentsUseCase constructor(
    private val downloadService: DownloadsService,
) {
    private val observableDownloads: MutableMap<UUID, Flow<FileState>> = mutableMapOf()

    protected fun observeAttachments(
        flow: Flow<Resource<List<Attachment2>>>
    ): Flow<Resource<List<Attachment2>>> {
        return flow.flatMapResourceFlow { attachments ->
            observeDownloadsFlow(attachments).map { states ->
                Resource.Success(
                    attachments.map { attachment ->
                        when (attachment) {
                            is FileAttachment2 -> if (attachment.state == FileState.Preview)
                                attachment.copy(state = states.getValue(attachment.id))
                            else attachment
                            is LinkAttachment2 -> attachment
                        }
                    }
                )
            }
        }
    }

    private fun observeDownloadsFlow(attachments: List<Attachment2>): Flow<Map<UUID, FileState>> {
        val unloadedFileAttachments = attachments.filterIsInstance<FileAttachment2>()
            .filter { it.state == FileState.Preview }

        // Remove missing downloads
        observableDownloads.keys.toList().forEach { key ->
            if (unloadedFileAttachments.any { it.id == key }) {
                downloadService.cancel(key)
                observableDownloads.remove(key)
            }
        }

        // Add observable downloads
        unloadedFileAttachments.forEach { fileAttachment ->
            val attachmentId = fileAttachment.id
            observableDownloads.putIfAbsent(
                attachmentId,
                downloadService.getDownloading(attachmentId)
            )
        }
        return if (observableDownloads.values.isEmpty()) {
            flowOf(emptyMap())
        } else{
            combine(observableDownloads.values) { states ->
                unloadedFileAttachments.zip(states) { unloadedFileAttachment, state ->
                    unloadedFileAttachment.id to state
                }.toMap()
            }
        }
    }
}