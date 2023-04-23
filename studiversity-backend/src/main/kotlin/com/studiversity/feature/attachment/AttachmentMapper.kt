package com.studiversity.feature.attachment

import com.denchic45.stuiversity.api.course.element.model.*
import com.studiversity.database.table.AttachmentDao

fun AttachmentDao.toHeader() = when (type) {
    AttachmentType.FILE -> toFileAttachmentHeader()
    AttachmentType.LINK -> toLinkAttachmentHeader()
}

fun AttachmentDao.toLinkAttachmentHeader() =
    LinkAttachmentHeader(id.value, LinkAttachmentResponse(id.value, url!!, name, thumbnailUrl))

fun AttachmentDao.toFileAttachmentHeader() =
    FileAttachmentHeader(id.value, FileItem(name, thumbnailUrl))

//fun ResultRow.toAttachment() = when (this[Attachments.type]) {
//    AttachmentType.FILE -> toFileAttachmentHeader()
//    AttachmentType.LINK -> toLinkAttachmentHeader()
//}

//fun ResultRow.toLinkAttachmentHeader() = LinkAttachmentHeader(
//    this[Attachments.id].value,
//    toLink()
//)


fun AttachmentDao.toFileResponse(byteArray: ByteArray) = FileAttachmentResponse(
    id.value,
    byteArray,
    name
)

fun AttachmentDao.toLinkResponse() = LinkAttachmentResponse(
    id = id.value,
    url = this.url!!,
    name = this.name,
    thumbnailUrl = this.thumbnailUrl
)


//fun ResultRow.toFileAttachmentHeader() = FileAttachmentHeader(
//    this[Attachments.id].value,
//    FileItem(
//        name = this[Attachments.name],
//        thumbnailUrl = this[Attachments.thumbnailUrl]
//    )
//)
