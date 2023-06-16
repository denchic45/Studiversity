package com.denchic45.studiversity.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.ui.timetable.state.toTimetableState
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.map
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.CoroutineScope
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

    val componentScope: CoroutineScope

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    fun onTodayClick() = selectedDate.update { LocalDate.now() }

    fun onNextWeekClick() {
        selectedDate.update { it.plusWeeks(1) }
    }

    fun onPreviousWeekClick() {
        selectedDate.update { it.minusWeeks(1) }
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    fun getTimetableStateOfLists(
//        bellSchedule: Flow<BellSchedule>,
//        timetableResource: Flow<Resource<List<List<PeriodResponse>>>>,
//    ): StateFlow<Resource<TimetableState>> {
//        return bellSchedule.flatMapLatest { schedule ->
//            selectedWeekOfYear.flatMapLatest { selectedWeek ->
//                timetableResource.mapResource {
//                    it.toTimetableState(selectedWeek, schedule)
//                }
//            }
//        }.stateInResource(componentScope)
//    }




}

class TimetableOwnerDelegate(
    componentContext: ComponentContext,
    initialDate: LocalDate = LocalDate.now(),
) : TimetableOwnerComponent {
    override val selectedDate = MutableStateFlow(initialDate)
    override val componentScope = componentContext.componentScope()

    override val selectedWeekOfYear = selectedDate.map(componentScope) {
        val week = it.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = it.get(WeekFields.ISO.weekBasedYear())
        "${year}_${week}".apply { println("WEEK owner: $this") }
    }
    override val mondayDate: StateFlow<LocalDate> = selectedDate.map(componentScope) {
        it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }
}