package com.denchic45.studiversity.api.submission.model


import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateSubmissionRequest(
    @Serializable(OptionalPropertySerializer::class)
    val content: OptionalProperty<SubmissionContent?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val grade: OptionalProperty<Short> = OptionalProperty.NotPresent
)