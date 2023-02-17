package com.denchic45.stuiversity.api.course.work.submission.model

import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object SubmissionSerializer :
    JsonContentPolymorphicSerializer<SubmissionResponse>(SubmissionResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out SubmissionResponse> =
        when (Json.decodeFromJsonElement<CourseElementType>(element.jsonObject.getValue("type"))) {
            CourseElementType.WORK -> AssignmentSubmissionResponse.serializer()
            CourseElementType.MATERIAL -> TODO()
        }
}