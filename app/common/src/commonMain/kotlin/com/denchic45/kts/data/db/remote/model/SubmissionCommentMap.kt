package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.util.FireMap
import java.util.*

data class SubmissionCommentMap(private val map: FireMap) : FireMap by map {
    val id: String by map
    val submissionId: String by map
    val content: String by map
    val authorId: String by map
    val createdDate: Date by map
}