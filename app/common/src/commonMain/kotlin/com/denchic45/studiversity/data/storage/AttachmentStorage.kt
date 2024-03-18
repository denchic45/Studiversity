package com.denchic45.studiversity.data.storage

import com.denchic45.studiversity.util.SystemDirs
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.github.michaelbull.result.onSuccess
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import okio.source
import java.util.*

@Inject
class AttachmentStorage(
    systemDirs: SystemDirs,
    private val attachmentApi: AttachmentApi,
) {

    val path: Path = systemDirs.fileDir / "attachments"

    private val fileSystem = FileSystem.SYSTEM

    init {
        FileSystem.SYSTEM.createDirectory(path)
    }

    private suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse> {
        return attachmentApi.download(attachmentId)
    }

    suspend fun downloadAndSave(attachmentId: UUID): ResponseResult<FileAttachmentResponse?> {
        return download(attachmentId).onSuccess { fileAttachment ->
            fileSystem.createDirectory(getFilePathById(fileAttachment.id))
            fileSystem.write(getFilePathByIdAndName(fileAttachment.id)) {
                writeAll(fileAttachment.inputStream.source())
            }
        }
    }

    private fun getFilePathById(attachmentId: UUID) = path / attachmentId.toString()

    fun getFilePathByIdAndName(attachmentId: UUID) = path / attachmentId.toString()

    fun delete(attachmentId: UUID) {
        fileSystem.deleteRecursively(getFilePathById(attachmentId))
    }
}