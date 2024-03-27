package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.database.table.AttachmentDao
import com.denchic45.studiversity.api.course.element.model.AttachmentType
import com.denchic45.studiversity.api.course.element.model.FileAttachmentHeader
import com.denchic45.studiversity.api.course.element.model.LinkAttachmentHeader

fun AttachmentDao.toAttachmentResponse() = when (type) {
    AttachmentType.FILE -> toFileAttachmentHeader()
    AttachmentType.LINK -> toLinkAttachmentHeader()
}

fun AttachmentDao.toLinkAttachmentHeader() = LinkAttachmentHeader(id.value, url!!, name, thumbnailUrl)

fun AttachmentDao.toFileAttachmentHeader() = FileAttachmentHeader(id.value, name, thumbnailUrl)