package com.denchic45.studiversity.data.db.remote.model

import com.denchic45.studiversity.util.MutableFireMap
import java.util.*

class EventMap(map: MutableFireMap) {
    var id: String by map
    var date: Date by map
    var position: Int by map
    var room: String by map
    var groupId: String by map
    var eventDetailsDoc: EventDetailsMap =
        EventDetailsMap((map["eventDetailsDoc"] as MutableFireMap))
}