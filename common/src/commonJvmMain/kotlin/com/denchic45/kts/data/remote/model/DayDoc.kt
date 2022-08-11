package com.denchic45.kts.data.remote.model

import com.denchic45.kts.data.domain.model.DocModel
import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.util.UUIDS
import java.util.*

data class DayDoc(
    val id: String,
    val date: Date,
    val startsAtZero: Boolean,
    var events: List<EventDoc>,
    val groupId: String,
) : DocModel {

    private constructor() : this(
        UUIDS.createShort(),
        Date(),
        false,
        emptyList(),
        ""
    )

    var timestamp: Date? = null

    val teacherIds: List<String>
        get() = events
            .filter { eventDoc: EventDoc -> eventDoc.eventDetailsDoc.eventType == EventType.LESSON }
            .map { eventDoc: EventDoc -> eventDoc.eventDetailsDoc.teacherIds!! }
            .flatten()

    val subjectIds: List<String>
        get() = events
            .filter { eventDoc: EventDoc -> eventDoc.eventDetailsDoc.eventType == EventType.LESSON }
            .map { eventDoc: EventDoc -> eventDoc.eventDetailsDoc.subjectId!! }
}