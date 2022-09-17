package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.DayMap
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

expect class EventRemoteDataSource {

    fun observeEventsOfGroupByDate(groupId: String, date: LocalDate): Flow<DayMap?>

    suspend fun findEventsOfGroupByDate(
        groupId: String,
        date: LocalDate,
    ): DayMap

    suspend fun updateEventsOfDay(dayMap: DayMap)

    fun observeEventsOfTeacherByDate(
        teacherId: String,
        date: LocalDate,
    ): Flow<List<DayMap>>

    suspend fun setDay(dayMap: DayMap)

    suspend fun findEventsOfGroupByDateRange(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): List<DayMap>

    fun observeEventsOfGroupByPreviousAndNextDates(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): Flow<List<DayMap>>
}