package com.denchic45.kts.data.remote.model

import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.MutableFireMap
import java.util.*

class DayMap(map: FireMap) {
    val id: String by map
    val date: Date by map
    val startsAtZero: Boolean by map
    val events: List<EventMap> = (map["events"] as List<MutableFireMap>).map { EventMap(it) }
    val groupId: String by map

    val timestamp: Date by map

    val teacherIds: List<String>
        get() = events
            .filter { EventMap: EventMap -> EventMap.eventDetailsDoc.type == EventType.LESSON.toString() }
            .map { EventMap: EventMap -> EventMap.eventDetailsDoc.teacherIds }
            .flatten()

    val subjectIds: List<String>
        get() = events
            .filter { EventMap: EventMap -> EventMap.eventDetailsDoc.type == EventType.LESSON.toString() }
            .map { EventMap: EventMap -> EventMap.eventDetailsDoc.subjectId }
}