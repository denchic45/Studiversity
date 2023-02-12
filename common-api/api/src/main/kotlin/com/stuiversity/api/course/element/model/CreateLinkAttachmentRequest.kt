package com.stuiversity.api.course.element.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateLinkAttachmentRequest(
    val url: String
)