package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.data.domain.model.TaskStatus
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.mapListOrEmpty
import com.denchic45.kts.util.mapOrDefault
import com.denchic45.kts.util.mapOrNull
import java.util.*

data class SubmissionMap(private val map: FireMap) : FireMap by map {
    val id: String by map
    val studentId: String by map
    val contentId: String by map
    val courseId: String by map
    val status: String by map
    val text: String by mapOrDefault("")
    val attachments: List<String> by mapListOrEmpty()
    val teacherId: String? by mapOrNull()
    val grade: Long? by mapOrNull()
    val gradedDate: Date? by mapOrNull()
    val timestamp: Date by map
    val rejectedDate: Date? by mapOrNull()
    val cause: String? by mapOrNull()
    val comments: List<FireMap> by mapListOrEmpty()
    val submittedDate: Date? by mapOrNull()

    val submitted: Boolean
        get() = TaskStatus.valueOf(status) != TaskStatus.NOT_SUBMITTED

    companion object {
        fun createNotSubmitted(
            id: String,
            studentId: String,
            contentId: String,
            courseId: String,
        ) = mutableMapOf(
            "id" to id,
            "studentId" to studentId,
            "contentId" to contentId,
            "courseId" to courseId,
            "status" to TaskStatus.NOT_SUBMITTED.name,
            "timestamp" to Date()
        )
    }
}