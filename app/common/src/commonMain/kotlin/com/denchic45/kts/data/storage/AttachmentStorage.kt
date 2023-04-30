package com.denchic45.kts.data.storage

import com.denchic45.kts.util.SystemDirs
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onSuccess
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import java.util.UUID

@Inject
class AttachmentStorage @javax.inject.Inject constructor(
    systemDirs: SystemDirs,
    private val attachmentApi: AttachmentApi,
) {

    val path: Path = systemDirs.fileDir.toOkioPath() / "attachments"

    private val fileSystem = FileSystem.SYSTEM

    init {
        FileSystem.SYSTEM.createDirectory(path)
    }

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