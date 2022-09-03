package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.DayMap
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

@me.tatarka.inject.annotations.Inject
actual class EventRemoteDataSource {
    actual fun observeEventsOfGroupByDate(groupId: String, date: LocalDate): Flow<DayMap> {
        TODO("Not yet implemented")
    }

    actual suspend fun findEventsOfGroupByDate(
        groupId: String,
        date: LocalDate,
    ): DayMap {
        TODO("Not yet implemented")
    }

    actual suspend fun updateEventsOfDay(dayMap: DayMap) {
    }

    actual fun observeEventsOfTeacherByDate(
        teacherId: String,
        date: LocalDate,
    ): Flow<List<DayMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun setDay(dayMap: DayMap) {
    }

    actual suspend fun findEventsOfGroupByDateRange(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): List<DayMap> {
        TODO("Not yet implemented")
    }

    actual fun observeEventsOfGroupByPreviousAndAfterDates(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): Flow<List<DayMap>> {
        TODO("Not yet implemented")
    }
}