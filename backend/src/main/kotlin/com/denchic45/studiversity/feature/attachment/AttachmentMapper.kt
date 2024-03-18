package com.denchic45.studiversity.feature.attachment

import com.denchic45.studiversity.database.table.AttachmentDao
import com.denchic45.stuiversity.api.course.element.model.AttachmentType
import com.denchic45.stuiversity.api.course.element.model.FileAttachmentHeader
import com.denchic45.stuiversity.api.course.element.model.LinkAttachmentHeader

fun AttachmentDao.toAttachmentResponse() = when (type) {
    AttachmentType.FILE -> toFileAttachmentHeader()
    AttachmentType.LINK -> toLinkAttachmentHeader()
}

fun AttachmentDao.toLinkAttachmentHeader() = LinkAttachmentHeader(id.value, url!!, name, thumbnailUrl)

fun AttachmentDao.toFileAttachmentHeader() = FileAttachmentHeader(id.value, name, thumbnailUrl)

//fun ResultRow.toAttachment() = when (this[Attachments.type]) {
//    AttachmentType.FILE -> toFileAttachmentHeader()
//    AttachmentType.LINK -> toLinkAttachmentHeader()
//}

//fun ResultRow.toLinkAttachmentHeader() = LinkAttachmentHeader(
//    this[Attachments.id].value,
//    toLink()
//)


//fun AttachmentDao.toFileResponse(byteArray: ByteArray) = FileAttachmentResponse(
//    id.value,
//    byteArray,
//    name
//)


//fun ResultRow.toFileAttachmentHeader() = FileAttachmentHeader(
//    this[Attachments.id].value,
//    FileItem(
//        name = this[Attachments.name],
//        thumbnailUrl = this[Attachments.thumbnailUrl]
//    )
//)
