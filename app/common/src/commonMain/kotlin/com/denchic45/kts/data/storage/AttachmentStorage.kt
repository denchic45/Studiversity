package com.denchic45.kts.data.storage


import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onSuccess
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.util.*

class AttachmentStorage @javax.inject.Inject constructor(private val attachmentApi: AttachmentApi) {

    val path: Path = "/attachments".toPath()
    private val fileSystem = FileSystem.SYSTEM

    private suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse?> {
        return attachmentApi.getById(attachmentId).map {
            if (it is FileAttachmentResponse) {
                it
            } else null
        }
    }

    suspend fun downloadAndSave(attachmentId: UUID): ResponseResult<FileAttachmentResponse?> {
        return download(attachmentId).onSuccess { fileAttachment ->
            fileAttachment?.let {
                fileSystem.write(getFilePath(it.id)) { write(it.bytes) }
            }
        }
    }

    fun getFilePath(attachmentId: UUID) = path / attachmentId.toString()
    fun delete(attachmentId: UUID) {
        fileSystem.delete(getFilePath(attachmentId))
    }
}