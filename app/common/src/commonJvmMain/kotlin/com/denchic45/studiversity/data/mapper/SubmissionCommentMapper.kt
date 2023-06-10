package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.SubmissionCommentEntity
import com.denchic45.studiversity.data.db.remote.model.SubmissionCommentMap

fun SubmissionCommentMap.domainToEntity() = SubmissionCommentEntity(
    comment_id = id,
    submission_id = submissionId,
    content = content,
    author_id = authorId,
    created_date = createdDate.time
)

fun List<SubmissionCommentMap>.docsToEntity() = map { it.domainToEntity() }