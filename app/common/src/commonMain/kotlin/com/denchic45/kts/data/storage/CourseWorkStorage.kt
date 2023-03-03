package com.denchic45.kts.data.storage

import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import okio.Path
import okio.Path.Companion.toPath
import java.util.*

class CourseWorkStorage:Storage() {
    override val path: Path = "/course_works".toPath()

    override suspend fun download(attachmentId: UUID): ResponseResult<FileAttachmentResponse> {
        TODO("Not yet implemented")
    }
}