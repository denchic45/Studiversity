package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.data.domain.model.Attachment

actual class SubmissionAttachmentRemoteStorage {
    actual suspend fun addSubmissionAttachments(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        TODO("Not yet implemented")
    }

    actual fun getAttachmentName(url: String): String {
        TODO("Not yet implemented")
    }

    actual suspend fun getAttachmentBytes(url: String): ByteArray {
        TODO("Not yet implemented")
    }

    actual suspend fun update(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        TODO("Not yet implemented")
    }

    actual suspend fun deleteFilesByContentId(contentId: String) {
    }

}