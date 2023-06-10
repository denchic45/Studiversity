package com.denchic45.studiversity.database.table


import com.denchic45.stuiversity.api.course.element.model.AttachmentType
import com.denchic45.studiversity.util.varcharMax
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Attachments : UUIDTable("attachment", "attachment_id") {
    val name = varcharMax("attachment_name")
    val url = varcharMax("url").nullable()
    val thumbnailUrl = varcharMax("thumbnail_url").nullable()
    val type = enumerationByName<AttachmentType>("type", 8)
    //    val path = varcharMax("path").nullable()
    val ownerId = uuid("owner_id")
//    val ownerType = enumerationByName<AttachmentOwner>("owner_type", 22)
}

class AttachmentDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<AttachmentDao>(Attachments)

    var name by Attachments.name
    var url by Attachments.url
    var thumbnailUrl by Attachments.thumbnailUrl
    var type by Attachments.type

    //    var path by Attachments.path
    var ownerId by Attachments.ownerId
//    var ownerType by Attachments.ownerType
}