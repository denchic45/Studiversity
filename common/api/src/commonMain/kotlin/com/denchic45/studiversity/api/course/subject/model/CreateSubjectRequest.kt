package com.denchic45.studiversity.api.course.subject.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSubjectRequest(
    val name: String,
    val shortname: String?
)