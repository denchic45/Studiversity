package com.denchic45.studiversity.api.submission.model

import kotlinx.serialization.Serializable

@Serializable
data class SubmissionByAuthor(
    val author: SubmissionAuthor,
    val submission: SubmissionResponse?
)
