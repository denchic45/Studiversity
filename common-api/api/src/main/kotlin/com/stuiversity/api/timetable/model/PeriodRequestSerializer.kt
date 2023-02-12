package com.stuiversity.api.timetable.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object PeriodRequestSerializer : JsonContentPolymorphicSerializer<PeriodRequest>(PeriodRequest::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PeriodRequest> {
        return when (Json.decodeFromJsonElement<PeriodType>(element.jsonObject.getValue("type"))) {
            PeriodType.LESSON -> LessonRequest.serializer()
            PeriodType.EVENT -> EventRequest.serializer()
        }
    }
}

