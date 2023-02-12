package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.data.domain.model.Attachment

expect class SubmissionAttachmentRemoteStorage {

    suspend fun addSubmissionAttachments(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String>

    fun getAttachmentName(url: String): String

    suspend fun getAttachmentBytes(url: String): ByteArray

    suspend fun update(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>
    ): List<String>

    suspend fun deleteFilesByContentId(contentId: String)
}