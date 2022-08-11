package com.denchic45.kts.data.remote.model

import com.denchic45.kts.data.domain.model.DocModel
import com.denchic45.kts.data.domain.model.EventType

data class EventDetailsDoc(
    var subjectId: String?,
    var teacherIds: List<String>?,
    var name: String?,
    var iconUrl: String?,
    var color: String?,
    var eventType: EventType,
) : DocModel {

    private constructor() : this("", emptyList(), "", "", "", EventType.EMPTY)

    companion object {
        fun createEmpty() = EventDetailsDoc()
    }
}