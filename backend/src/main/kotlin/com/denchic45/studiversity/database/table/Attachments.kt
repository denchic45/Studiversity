package com.denchic45.studiversity.database.table


import com.denchic45.stuiversity.api.course.element.model.AttachmentType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Attachments : UUIDTable("attachment", "attachment_id") {
    val name = text("attachment_name")
    val url = text("url").nullable()
    val thumbnailUrl = text("thumbnail_url").nullable()
    val type = enumerationByName<AttachmentType>("type", 8)

    //    val path = text("path").nullable()
    val resourceId = uuid("resource_id")
    val resourceType = text("resource_type")
}

class AttachmentDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AttachmentDao>(Attachments)

    var name by Attachments.name
    var url by Attachments.url
    var thumbnailUrl by Attachments.thumbnailUrl
    var type by Attachments.type

    //    var path by Attachments.path
    var resourceId by Attachments.resourceId
    var resourceType by Attachments.resourceType
}