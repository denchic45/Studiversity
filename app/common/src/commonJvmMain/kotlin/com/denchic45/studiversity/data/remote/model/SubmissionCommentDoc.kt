package com.denchic45.studiversity.data.remote.model

import java.util.*

class SubmissionCommentDoc(
    val id: String,
    val submissionId: String,
    val content: String,
    val authorId: String,
    val createdDate: Date
)