package com.denchic45.studiversity.data.db.remote.model

import com.denchic45.studiversity.domain.model.EventType
import com.denchic45.studiversity.util.MutableFireMap
import com.denchic45.studiversity.util.mapListOrEmpty
import java.util.Date

data class DayMap(private val map: MutableFireMap) : MutableFireMap by map {
    var id: String by map
    var date: Date by map
    var startsAtZero: Boolean by map
    var events: List<MutableFireMap> by mapListOrEmpty()
    var groupId: String by map
    var timestamp: Date by map

    val teacherIds: List<String>
        get() = events
            .filter { eventMap: MutableFireMap -> EventMap(eventMap).eventDetailsDoc.eventType == EventType.LESSON.toString() }
            .map { eventMap: MutableFireMap -> EventMap(eventMap).eventDetailsDoc.teacherIds!! }
            .flatten()
    val subjectIds: List<String>
        get() = events
            .filter { eventMap: MutableFireMap -> EventMap(eventMap).eventDetailsDoc.eventType == EventType.LESSON.toString() }
            .map { eventMap: MutableFireMap -> EventMap(eventMap).eventDetailsDoc.subjectId!! }
}