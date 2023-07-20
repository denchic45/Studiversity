package com.denchic45.studiversity.data.remote.model

import com.denchic45.studiversity.domain.model.DocModel
import java.util.Date

data class EventDoc(
    var id: String,
    var date: Date,
    var position: Int,
    var room: String,
    var groupId: String,
    var eventDetailsDoc: EventDetailsDoc,
) : DocModel {

    private constructor() : this("", Date(), -1, "", "", EventDetailsDoc.createEmpty())

    companion object {
        fun createEmpty() = EventDoc()
    }
}