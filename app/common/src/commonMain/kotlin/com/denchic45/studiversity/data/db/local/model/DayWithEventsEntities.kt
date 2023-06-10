package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.DayEntity
import com.denchic45.studiversity.EventWithSubjectAndGroupAndTeachers

data class DayWithEventsEntities(
    var dayEntity: DayEntity,
    var eventEntities: List<EventWithSubjectAndGroupAndTeachers>
)