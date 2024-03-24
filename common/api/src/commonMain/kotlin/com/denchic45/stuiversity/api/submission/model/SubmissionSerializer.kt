package com.denchic45.stuiversity.api.submission.model

import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

object SubmissionSerializer :
    JsonContentPolymorphicSerializer<SubmissionResponse>(SubmissionResponse::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<WorkSubmissionResponse> =
        when (Json.decodeFromJsonElement<CourseElementType>(element.jsonObject.getValue("type"))) {
            CourseElementType.WORK -> WorkSubmissionResponse.serializer()
            CourseElementType.MATERIAL -> TODO()
        }
}