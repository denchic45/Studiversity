package com.denchic45.studiversity.feature.attachment

import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.source
import java.io.InputStream
import java.util.*

class AttachmentFileStorage(private val fileSystem: FileSystem) {
    private val attachmentsDir = "data/attachments".toPath()

    fun writeFile(fileName: String, request: CreateFileRequest) {
        fileSystem.write(attachmentsDir / fileName) {
            writeAll(request.inputStream.source())
        }
    }

    fun exists(attachmentId: UUID) = fileSystem.exists(attachmentsDir / attachmentId.toString())

    fun getSource(attachmentId: UUID): InputStream {
        return fileSystem.source(attachmentsDir / attachmentId.toString()).buffer().inputStream()
    }

    fun delete(name: UUID) {
        fileSystem.delete(attachmentsDir / name.toString())
    }

    fun deleteAll(attachmentIds: List<UUID>) {
        attachmentIds.forEach { fileSystem.delete(it.toString().toPath()) }
    }
}