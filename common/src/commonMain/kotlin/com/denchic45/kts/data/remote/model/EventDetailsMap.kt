package com.denchic45.kts.data.remote.model

import com.denchic45.kts.util.FireMap

class EventDetailsMap(map: FireMap) {
    val subjectId: String by map
    val teacherIds: List<String> by map
    val name: String by map
    val iconUrl: String by map
    val color: String by map
    val type: String by map
}