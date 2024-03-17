package com.denchic45.stuiversity.api.course.element.model

//object AttachmentSerializer : JsonContentPolymorphicSerializer<AttachmentResponse>(AttachmentResponse::class) {
//    override fun selectDeserializer(element: JsonElement): KSerializer<out AttachmentResponse> {
//        return when {
//            element.jsonObject.containsKey("bytes") -> FileAttachmentResponse.serializer()
//            element.jsonObject.containsKey("url") -> LinkAttachmentResponse.serializer()
//            else -> throw IllegalStateException()
//        }
//    }
//}