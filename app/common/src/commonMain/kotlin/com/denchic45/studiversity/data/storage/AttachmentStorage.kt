package com.denchic45.studiversity.data.storage

import com.denchic45.studiversity.util.SystemDirs
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onSuccess
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path
import java.util.UUID

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
                fileSystem.createDirectory(getFilePathById(fileAttachment.id))
                fileSystem.write(
                    getFilePathByIdAndName(
                        it.id,
                        fileAttachment.name
                    )
                ) { write(it.bytes) }
            }
        }
    }

    private fun getFilePathById(attachmentId: UUID) = path / attachmentId.toString()

    fun getFilePathByIdAndName(attachmentId: UUID, name: String) =
        path / attachmentId.toString() / name

    fun delete(attachmentId: UUID) {
        fileSystem.deleteRecursively(getFilePathById(attachmentId))
    }
}