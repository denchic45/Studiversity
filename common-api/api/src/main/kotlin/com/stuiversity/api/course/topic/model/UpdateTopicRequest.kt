package com.stuiversity.api.course.topic.model

import com.stuiversity.util.OptionalProperty
import com.stuiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTopicRequest(
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent
)
