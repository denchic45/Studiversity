package com.denchic45.studiversity.api.course.element.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

object AttachmentHeaderSerializer : JsonContentPolymorphicSerializer<AttachmentHeader>(AttachmentHeader::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out AttachmentHeader> {
        return when (Json.decodeFromJsonElement<AttachmentType>(element.jsonObject.getValue("type"))) {
            AttachmentType.FILE -> FileAttachmentHeader.serializer()
            AttachmentType.LINK -> LinkAttachmentHeader.serializer()
        }
    }
}