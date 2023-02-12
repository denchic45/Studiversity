package com.denchic45.kts.data.db.local.model

import com.denchic45.kts.DayEntity
import com.denchic45.kts.EventWithSubjectAndGroupAndTeachers

data class DayWithEventsEntities(
    var dayEntity: DayEntity,
    var eventEntities: List<EventWithSubjectAndGroupAndTeachers>
)