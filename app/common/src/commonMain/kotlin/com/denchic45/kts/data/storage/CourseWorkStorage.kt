package com.denchic45.kts.data.storage

import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.toResource
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentResponse
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import okio.Path
import okio.Path.Companion.toPath
import java.util.*

class CourseWorkStorage(private val courseWorkApi: CourseWorkApi) : AttachmentStorage() {
    override val path: Path = "/course_works".toPath()

    override suspend fun download(attachmentId: UUID): Resource<FileAttachmentResponse> {
        return courseWorkApi.getAttachment(attachmentId).toResource().map { it as FileAttachmentResponse }
    }
}