package com.denchic45.kts.data.storage

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.storage.remote.ContentAttachmentRemoteStorage
import com.denchic45.kts.data.storage.local.ContentAttachmentLocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContentAttachmentStorage @Inject constructor(
    private val contentAttachmentRemoteStorage: ContentAttachmentRemoteStorage,
    private val contentAttachmentLocalStorage: ContentAttachmentLocalStorage,
) {

    suspend fun get(contentId: String, urls: List<String>): List<Attachment> {
        return withContext(Dispatchers.IO) {
            if (urls.isEmpty())
                emptyList()
            else urls.map { url ->
                val localFile = contentAttachmentLocalStorage.get(contentId, url)
                if (localFile == null) {
                    contentAttachmentRemoteStorage.get(url).run {
                        Attachment(
                            contentAttachmentLocalStorage.saveFile(
                                contentId = contentId,
                                name = first,
                                bytes = second
                            )
                        )
                    }
                } else {
                    Attachment(file = localFile)
                }
            }
        }
    }

    suspend fun addContentAttachments(
        parentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        return contentAttachmentRemoteStorage.addContentAttachments(parentId, attachments)
    }

    suspend fun update(id: String, attachments: List<Attachment>): List<String> {
        return contentAttachmentRemoteStorage.update(id, attachments)
    }

    suspend fun deleteFilesByContentId(contentId: String) {
        deleteFromLocal(contentId)
        contentAttachmentRemoteStorage.deleteByContentId(contentId)
    }

    fun deleteFromLocal(contentId: String) {
        return contentAttachmentLocalStorage.deleteFromLocal(contentId)
    }
}

