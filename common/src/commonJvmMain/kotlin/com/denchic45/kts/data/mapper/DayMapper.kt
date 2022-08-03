package com.denchic45.kts.data.mapper

import com.denchic45.kts.DayEntity
import com.denchic45.kts.data.local.model.DayWithEventsEntities
import com.denchic45.kts.data.remote.model.DayDoc
import com.denchic45.kts.data.remote.model.DayMap
import com.denchic45.kts.data.remote.model.EventDoc
import com.denchic45.kts.domain.model.EventsOfDay
import com.denchic45.kts.util.*
import java.time.ZoneOffset
import java.util.*

fun DayMap.mapToEntity() = DayEntity(
    day_id = id,
    date = date.toString(DatePatterns.yyy_MM_dd),
    start_at_zero = startsAtZero,
    group_id = groupId
)

fun DayWithEventsEntities.entityToUserDomain() = EventsOfDay(
    date = dayEntity.date.toLocalDate(DatePatterns.yyy_MM_dd),
    _events = eventEntities.entitiesToDomains()
)

fun EventsOfDay.domainToMap(groupId: String):MutableFireMap = mutableMapOf(
    "id" to id,
    "date" to date.toDate(),
    "startsAtZero" to startsAtZero,
    "events" to events.domainsToMaps(),
    "groupId" to groupId,
    "timestamp" to Date()
)