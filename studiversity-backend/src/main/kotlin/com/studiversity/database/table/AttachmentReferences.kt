package com.studiversity.database.table

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

    val consumerId = uuid("consumer_id")
}

class AttachmentReferenceDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<AttachmentReferenceDao>(AttachmentReferences)

    var attachment by AttachmentDao referencedOn AttachmentReferences.attachmentId
    var consumerId by AttachmentReferences.consumerId
}