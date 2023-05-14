package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.ui.timetable.state.TimetableState
import com.denchic45.kts.ui.timetable.state.toTimetableState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

interface TimetableOwnerComponent {
    val selectedDate: MutableStateFlow<LocalDate>
    val selectedWeekOfYear: StateFlow<String>
    val mondayDate: StateFlow<LocalDate>

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    fun onTodayClick() = selectedDate.update { LocalDate.now() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTimetableState(
        bellSchedule: Flow<BellSchedule>,
        selectedWeekOfYear: Flow<String>,
        timetableResource: Flow<Resource<List<List<PeriodResponse>>>>,
    ): Flow<Resource<TimetableState>> {
        return bellSchedule.flatMapLatest { schedule ->
            selectedWeekOfYear.flatMapLatest { selectedWeek ->
                timetableResource.mapResource {
                    it.toTimetableState(selectedWeek, schedule)
                }
            }
        }
    }


    fun getTimetableOfResponseState(
        bellSchedule: Flow<BellSchedule>,
        selectedWeekOfYear: Flow<String>,
        timetableResource: Flow<Resource<TimetableResponse>>,
    ): Flow<Resource<TimetableState>> {
        return getTimetableState(
            bellSchedule,
            selectedWeekOfYear,
            timetableResource.mapResource { it.days })
    }

}

class TimetableOwnerDelegate(
    componentContext: ComponentContext,
    private val initialDate: LocalDate = LocalDate.now(),
) :
    TimetableOwnerComponent {
    override val selectedDate = MutableStateFlow(initialDate)
    private val coroutineScope = componentContext.componentScope()

    override val selectedWeekOfYear = selectedDate.map(coroutineScope) {
        val week = it.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = it.get(WeekFields.ISO.weekBasedYear())
        "${year}_${week}".apply { println("WEEK owner: $this") }
    }
    override val mondayDate: StateFlow<LocalDate> = selectedDate.map(coroutineScope) {
        it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
}