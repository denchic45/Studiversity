package com.denchic45.stuiversity.api.course.work.submission.model

import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

object SubmissionSerializer :
    JsonContentPolymorphicSerializer<SubmissionResponse>(SubmissionResponse::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<WorkSubmissionResponse> =
        when (Json.decodeFromJsonElement<CourseElementType>(element.jsonObject.getValue("type"))) {
            CourseElementType.WORK -> WorkSubmissionResponse.serializer()
            CourseElementType.MATERIAL -> TODO()
        }
}