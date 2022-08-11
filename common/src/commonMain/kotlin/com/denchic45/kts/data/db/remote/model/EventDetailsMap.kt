package com.denchic45.kts.data.db.remote.model

import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.mapOrNull

class EventDetailsMap(override val map: FireMap):MapWrapper {
    val subjectId: String? by mapOrNull()
    val teacherIds: List<String>? by mapOrNull()
    val name: String? by mapOrNull()
    val iconUrl: String? by mapOrNull()
    val color: String? by mapOrNull()
    val eventType: String by map
}