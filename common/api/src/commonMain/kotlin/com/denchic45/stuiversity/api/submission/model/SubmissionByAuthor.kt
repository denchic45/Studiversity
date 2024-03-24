package com.denchic45.stuiversity.api.submission.model

import kotlinx.serialization.Serializable

@Serializable
data class SubmissionByAuthor(
    val author: SubmissionAuthor,
    val submission: SubmissionResponse?
)
