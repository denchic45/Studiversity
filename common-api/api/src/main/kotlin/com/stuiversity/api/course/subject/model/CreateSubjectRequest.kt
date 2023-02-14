package com.stuiversity.api.course.subject.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSubjectRequest(
    val name: String,
    val shortname: String,
    val iconName: String
)
