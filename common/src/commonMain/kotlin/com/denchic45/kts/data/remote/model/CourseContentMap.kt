package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.mapOrNull
import java.util.*

class CourseContentMap(override val map: FireMap) : MapWrapper {
    val id: String by map
    val courseId: String = ""
    val sectionId: String = ""
    val name: String by map
    val description: String by map
    val commentsEnabled: Boolean by map
    val attachments: List<String> by map
    val order: Long by map
    val createdDate: Date by map
    val timestamp: Date by map
    val deleted: Boolean by map
    val contentType: String by map
    val completionDate: Date? by mapOrNull()
    val weekDate: String? by mapOrNull()
    val contentDetails: FireMap by map

//    constructor(
//        id: String,
//        courseId: String = "",
//        sectionId: String = "",
//        name: String,
//        description: String,
//        commentsEnabled: Boolean,
//        attachments: List<String>,
//        order: Long,
//        createdDate: Date,
//        timestamp: Date,
//        deleted: Boolean,
//        contentType: String,
//        completionDate: Date?,
//        weekDate: String?,
//        contentDetails: FireMap,
//    ) : this(mutableMapOf()) {
//        this.id = id
//        this.courseId = courseId
//        this.sectionId = sectionId
//        this.name = name
//        this.description = description
//        this.commentsEnabled = commentsEnabled
//        this.attachments = attachments
//        this.order = order
//        this.createdDate = createdDate
//        this.timestamp = timestamp
//        this.deleted = deleted
//        this.contentType = contentType
//        this.completionDate = completionDate
//        this.weekDate = weekDate
//        this.contentDetails = contentDetails
//    }

    val comments: List<ContentCommentMap> = (map["comments"] as List<FireMap>)
        .map(::ContentCommentMap)
    val submissions: Map<String, SubmissionMap> = (map["submissions"] as Map<String, FireMap>)
        .map { Pair(it.key, SubmissionMap(it.value)) }
        .toMap()
}