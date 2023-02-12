package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.data.domain.model.Attachment

expect class ContentAttachmentRemoteStorage {
    suspend fun get(url: String): ByteArray
    suspend fun deleteByContentId(contentId: String)
    suspend fun addContentAttachments(
        parentId: String,
        attachments: List<Attachment>,
    ): List<String>

    suspend fun update(
        id: String,
        attachments: List<Attachment>,
    ): List<String>

    fun getAttachmentName(url: String): String
}