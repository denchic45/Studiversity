package com.denchic45.studiversity.api.course.material.model


import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import com.denchic45.studiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class UpdateCourseMaterialRequest(
    @Serializable(OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val description: OptionalProperty<String?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val topicId: OptionalProperty<@Serializable(UUIDSerializer::class) UUID?> = OptionalProperty.NotPresent
)