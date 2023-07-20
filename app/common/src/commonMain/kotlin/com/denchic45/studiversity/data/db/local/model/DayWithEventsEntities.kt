package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.entity.DayEntity
import com.denchic45.studiversity.entity.EventWithSubjectAndGroupAndTeachers

data class DayWithEventsEntities(
    var dayEntity: DayEntity,
    var eventEntities: List<EventWithSubjectAndGroupAndTeachers>
)