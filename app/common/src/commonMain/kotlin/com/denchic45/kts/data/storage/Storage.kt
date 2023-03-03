package com.denchic45.kts.data.storage


import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.github.michaelbull.result.onSuccess
import okio.FileSystem
import okio.Path
import java.util.*

abstract class Storage {

    abstract val path: Path

    protected abstract suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse>

    suspend fun downloadAndSave(attachmentId: UUID): ResponseResult<FileAttachmentResponse> {
        return download(attachmentId).onSuccess {
            FileSystem.SYSTEM.write(getFilePath(it.name)) { write(it.bytes) }
        }
    }

    fun getFilePath(name: String) = path / name
}

class TestStorage(override val path: Path) : Storage() {
    override suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse> {
        TODO("Not yet implemented")
    }
}