package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.MapValueListOrEmptyDelegate
import com.denchic45.kts.util.mapListOrEmpty
import com.denchic45.kts.util.mapOrNull
import java.util.*

class SubmissionMap(override val map: FireMap) : MapWrapper {
    val id: String by map
    val studentId: String by map
    val contentId: String by map
    val courseId: String by map
    val status: String by map
    val text: String by map
    val attachments: List<String> by map
    val teacherId: String by map
    val grade: Int by map
    val gradedDate: Date by map
    val timestamp: Date by map
    val rejectedDate: Date by map
    val cause: String by map
    val comments: List<FireMap> by mapListOrEmpty()
    val submittedDate: Date? by mapOrNull()
}