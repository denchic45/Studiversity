package com.denchic45.stuiversity.api.course.work.submission.model


import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateSubmissionRequest(
    @Serializable(OptionalPropertySerializer::class)
    val content: OptionalProperty<SubmissionContent?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val grade: OptionalProperty<Short> = OptionalProperty.NotPresent
)