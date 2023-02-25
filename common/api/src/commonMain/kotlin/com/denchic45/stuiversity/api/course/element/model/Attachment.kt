package com.denchic45.stuiversity.api.course.element.model

import kotlinx.serialization.Serializable

sealed interface Attachment

class FileAttachment(
    val bytes: ByteArray,
    val name: String,
) : Attachment

@Serializable
data class Link(
    val url: String,
    val name: String,
    val thumbnailUrl: String?
) : Attachment