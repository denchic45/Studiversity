package com.denchic45.kts.data.mapper

import com.denchic45.kts.ContentCommentEntity
import com.denchic45.kts.data.remote.model.ContentCommentMap

fun ContentCommentMap.domainToEntity() = ContentCommentEntity(
    comment_id = id,
    content_id = contentId,
    content = content,
    author_id = authorId,
    created_date = createdDate.time
)

fun List<ContentCommentMap>.docsToEntity() = map { it.domainToEntity() }