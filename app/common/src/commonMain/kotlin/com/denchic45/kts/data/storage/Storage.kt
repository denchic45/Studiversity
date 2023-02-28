package com.denchic45.kts.data.storage


import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.github.michaelbull.result.onSuccess
import okio.FileSystem
import okio.Path

abstract class Storage {

    abstract val path: Path

    protected abstract fun download(): ResponseResult<FileAttachmentResponse>

    fun downloadAndSave() {
        download().onSuccess {
            FileSystem.SYSTEM.write(path / it.name) { write(it.bytes) }
        }
    }
}