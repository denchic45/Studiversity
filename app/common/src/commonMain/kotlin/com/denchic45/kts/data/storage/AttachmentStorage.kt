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

class AttachmentStorage(private val attachmentApi: AttachmentApi) {

    val path: Path = "/attachments".toPath()

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
                FileSystem.SYSTEM.write(getFilePath(it.name)) { write(it.bytes) }
            }
        }
    }

    fun getFilePath(name: String) = path / name
}