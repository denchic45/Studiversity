package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.mapOrNull

data class EventDetailsMap(private val map: FireMap) : FireMap by map {
    val subjectId: String? by mapOrNull()
    val teacherIds: List<String>? by mapOrNull()
    val name: String? by mapOrNull()
    val iconName: String? by mapOrNull()
    val color: String? by mapOrNull()
    val eventType: String by map
}