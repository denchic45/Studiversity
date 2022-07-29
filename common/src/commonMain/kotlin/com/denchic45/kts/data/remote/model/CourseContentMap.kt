package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import java.util.*

class CourseContentMap(map: FireMap) {
    val id: String by map
    val courseId: String = ""
    val sectionId: String = ""
    val name: String by map
    val description: String by map
    val commentsEnabled: Boolean by map
    val attachments: List<String> by map
    val order: Long by map
    val createdDate: Date by map
    val timestamp: Date? by map
    val comments: List<ContentCommentMap> =
        (map["comments"] as List<FireMap>).map { ContentCommentMap(it) }
    val submissions: Map<String, SubmissionMap> = (map["submissions"] as Map<String, FireMap>)
        .map { Pair(it.key, SubmissionMap(it.value)) }
        .toMap()
    val deleted: Boolean by map
    val contentType: String by map
    val completionDate: Date? by map
    val weekDate: String? by map
    val contentDetails: Map<String, Any?> by map
}