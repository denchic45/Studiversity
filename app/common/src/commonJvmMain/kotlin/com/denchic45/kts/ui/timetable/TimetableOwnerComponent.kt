package com.denchic45.kts.ui.timetable

import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.toDayTimetableViewState
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface TimetableOwnerComponent {

    fun dayViewStateFlow(
        selectedDate: LocalDate,
        schedule: BellSchedule,
        timetableResource:StateFlow<Resource<TimetableResponse>>
    ): Flow<Resource<DayTimetableViewState>> {
        val dayOfWeek = selectedDate.dayOfWeek.ordinal
        return timetableResource.mapResource {
            (if (dayOfWeek == 6) emptyList()
            else it.days[dayOfWeek]).toDayTimetableViewState(selectedDate, schedule, false)
        }
    }
}