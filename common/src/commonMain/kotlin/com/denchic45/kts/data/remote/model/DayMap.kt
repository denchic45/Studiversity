package com.denchic45.kts.data.remote.model

import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.util.MutableFireMap
import java.util.*

class DayMap(override val map: MutableFireMap):MutableMapWrapper {
    var id: String by map
    var date: Date by map
    var startsAtZero: Boolean by map
    var events: List<EventMap> = (map["events"] as List<MutableFireMap>).map { EventMap(it) }
    var groupId: String by map
    var timestamp: Date by map

    val teacherIds: List<String>
        get() = events
            .filter { EventMap: EventMap -> EventMap.eventDetailsDoc.eventType == EventType.LESSON.toString() }
            .map { EventMap: EventMap -> EventMap.eventDetailsDoc.teacherIds!! }
            .flatten()
    val subjectIds: List<String>
        get() = events
            .filter { EventMap: EventMap -> EventMap.eventDetailsDoc.eventType == EventType.LESSON.toString() }
            .map { EventMap: EventMap -> EventMap.eventDetailsDoc.subjectId!! }
}