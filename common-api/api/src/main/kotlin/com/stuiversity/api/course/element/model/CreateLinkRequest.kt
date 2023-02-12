package com.stuiversity.api.course.element.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateLinkRequest(val url: String) : AttachmentRequest