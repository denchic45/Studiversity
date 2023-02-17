package com.denchic45.stuiversity.api.timetable.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object PeriodResponseSerializer : JsonContentPolymorphicSerializer<PeriodResponse>(PeriodResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out PeriodResponse> {
        return when (Json.decodeFromJsonElement<PeriodType>(element.jsonObject.getValue("type"))) {
            PeriodType.LESSON -> LessonResponse.serializer()
            PeriodType.EVENT -> EventResponse.serializer()
        }
    }
}