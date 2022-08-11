package com.denchic45.kts.data.storage

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.storage.local.SubmissionAttachmentLocalStorage
import com.denchic45.kts.data.storage.remote.SubmissionAttachmentRemoteStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubmissionAttachmentStorage @Inject constructor(
    private val submissionAttachmentRemoteStorage: SubmissionAttachmentRemoteStorage,
    private val submissionAttachmentLocalStorage: SubmissionAttachmentLocalStorage,
) {

     suspend fun addSubmissionAttachments(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        return submissionAttachmentRemoteStorage.addSubmissionAttachments(contentId,
            studentId,
            attachments)
    }

    suspend fun get(contentId: String, studentId: String, urls: List<String>): List<Attachment> {
        return withContext(Dispatchers.IO) {
            if (urls.isNotEmpty()) {
                val localFiles = submissionAttachmentLocalStorage.getByContentIdAndStudentId(
                    contentId,
                    studentId
                )
                urls.map { url ->
                    val name = submissionAttachmentRemoteStorage.getAttachmentName(url)
                    Attachment(
                        file = localFiles.firstOrNull { it.name == name } ?: run {
                            submissionAttachmentLocalStorage.save(contentId,
                                studentId,
                                name,
                                submissionAttachmentRemoteStorage.getAttachmentBytes(url))
                        }
                    )
                }
            } else emptyList()
        }
    }

    suspend fun update(
        contentId: String,
        studentId: String,
        attachments: List<Attachment>,
    ): List<String> {
        return submissionAttachmentRemoteStorage.update(contentId, studentId, attachments)
    }

    suspend fun deleteFilesByContentId(contentId: String) {
        return submissionAttachmentRemoteStorage.deleteFilesByContentId(contentId)
    }

    fun deleteFromLocal(contentId: String) {
        submissionAttachmentLocalStorage.deleteByContentId(contentId)
    }
}