package com.denchic45.stuiversity.api.submission.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object SubmissionContentSerializer :
    JsonContentPolymorphicSerializer<SubmissionContent>(SubmissionContent::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<WorkSubmissionContent> {
        // TODO: Rewrite serialization
        return when {
            element.jsonObject["attachments"] != null -> WorkSubmissionContent.serializer()
            else -> TODO("UNKNOWN_CONTENT_TYPE")
        }
    }
}