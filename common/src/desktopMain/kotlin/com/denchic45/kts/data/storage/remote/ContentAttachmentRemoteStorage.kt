package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.data.domain.model.Attachment

actual class ContentAttachmentRemoteStorage {
    actual suspend fun get(url: String): Pair<String, ByteArray> {
        TODO("Not yet implemented")
    }

    actual suspend fun deleteByContentId(contentId: String) {
    }

    actual suspend fun addContentAttachments(
        parentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        TODO("Not yet implemented")
    }

    actual suspend fun update(
        id: String,
        attachments: List<Attachment>,
    ): List<String> {
        TODO("Not yet implemented")
    }

}