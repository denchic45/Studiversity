package com.denchic45.studiversity.domain.model

import java.time.LocalDate

//data class GroupTimetable(
//    val groupHeader: GroupHeader,
//    val weekEvents: List<EventsOfDay>,
//) {
//
//    fun updateEventsOfDay(eventsOfDay: EventsOfDay): GroupTimetable {
//        return copy(weekEvents = weekEvents.toMutableList()
//            .apply { set(eventsOfDay.dayOfWeek - 1, eventsOfDay) })
//    }
//
//    fun getByDayOfWeek(dayOfWeek: Int): EventsOfDay {
//        return weekEvents[dayOfWeek - 1]
//    }
//
//    companion object {
//        fun createEmpty(groupHeader: GroupHeader, mondayDate: LocalDate): GroupTimetable {
//            return GroupTimetable(
//                groupHeader,
//                List(6) { EventsOfDay.createEmpty(mondayDate.plusDays(it.toLong())) }
//            )
//        }
//    }
//}