package com.denchic45.studiversity.data.mapper

import com.denchic45.studiversity.data.db.remote.model.ContentCommentMap
import com.denchic45.studiversity.entity.ContentCommentEntity

fun ContentCommentMap.domainToEntity() = ContentCommentEntity(
    comment_id = id,
    content_id = contentId,
    content = content,
    author_id = authorId,
    created_date = createdDate.time
)

fun List<ContentCommentMap>.docsToEntity() = map { it.domainToEntity() }