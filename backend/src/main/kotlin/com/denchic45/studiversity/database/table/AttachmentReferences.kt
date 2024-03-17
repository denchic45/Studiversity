package com.denchic45.studiversity.database.table

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object AttachmentReferences : LongIdTable("attachment_reference", "reference_id") {
    val attachmentId = reference(
        name = "attachment_id",
        foreign = Attachments,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val resourceId = uuid("resource_id")
}

class AttachmentReferenceDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AttachmentReferenceDao>(AttachmentReferences)

    var attachment by AttachmentDao referencedOn AttachmentReferences.attachmentId
    var resourceId by AttachmentReferences.resourceId
}