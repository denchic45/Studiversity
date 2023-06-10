//package com.denchic45.studiversity.data.mapper
//
//import com.denchic45.studiversity.DayEntity
//import com.denchic45.studiversity.data.db.local.model.DayWithEventsEntities
//import com.denchic45.studiversity.data.db.remote.model.DayMap
//import com.denchic45.studiversity.domain.model.EventsOfDay
//import com.denchic45.studiversity.util.*
//import com.denchic45.stuiversity.util.DatePatterns
//import com.denchic45.stuiversity.util.toDate
//import com.denchic45.stuiversity.util.toLocalDate
//import com.denchic45.stuiversity.util.toString
//import java.util.*
//
//fun DayMap.mapToEntity() = DayEntity(
//    day_id = id,
//    date = date.toString(DatePatterns.yyy_MM_dd),
//    start_at_zero = startsAtZero,
//    group_id = groupId
//)
//
//fun DayWithEventsEntities.entityToUserDomain() = EventsOfDay(
//    date = dayEntity.date.toLocalDate(DatePatterns.yyy_MM_dd),
//    events = eventEntities.entitiesToDomains(),
//    id = dayEntity.day_id,
//    startsAtZero = dayEntity.start_at_zero
//)
//
//fun EventsOfDay.domainToMap(groupId: String): MutableFireMap = mutableMapOf(
//    "id" to id,
//    "date" to date.toDate(),
//    "startsAtZero" to startsAtZero,
//    "events" to events.domainsToMaps(),
//    "groupId" to groupId,
//    "timestamp" to Date()
//)