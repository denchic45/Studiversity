package com.studiversity.feature.attachment

import com.studiversity.database.table.AttachmentDao
import com.studiversity.database.table.Attachments
import com.stuiversity.api.course.element.model.*
import org.jetbrains.exposed.sql.ResultRow

fun AttachmentDao.toResponse() = when (type) {
    AttachmentType.FILE -> toFileAttachmentHeader()
    AttachmentType.LINK -> toLinkAttachmentHeader()
}

fun AttachmentDao.toLinkAttachmentHeader() =
    LinkAttachmentHeader(id.value, Link(url!!, name, thumbnailUrl))

fun AttachmentDao.toFileAttachmentHeader() =
    FileAttachmentHeader(id.value, FileItem(name, thumbnailUrl))

fun ResultRow.toAttachment() = when (this[Attachments.type]) {
    AttachmentType.FILE -> toFileAttachmentHeader()
    AttachmentType.LINK -> toLinkAttachmentHeader()
}

fun ResultRow.toLinkAttachmentHeader() = LinkAttachmentHeader(
    this[Attachments.id].value,
    toLink()
)

fun ResultRow.toLink() = Link(
    url = this[Attachments.url]!!,
    name = this[Attachments.name],
    thumbnailUrl = this[Attachments.thumbnailUrl]
)


fun ResultRow.toFileAttachmentHeader() = FileAttachmentHeader(
    this[Attachments.id].value,
    FileItem(
        name = this[Attachments.name],
        thumbnailUrl = this[Attachments.thumbnailUrl]
    )
)
