package com.denchic45.studiversity.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

interface TimetableOwnerComponent {
    val componentScope: CoroutineScope
    val selectedDate: MutableStateFlow<LocalDate>
    val mondayDate: StateFlow<LocalDate>
    val selectedWeekOfYear: StateFlow<String>

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    fun onTodayClick() = selectedDate.update {
        LocalDate.now()
    }

    fun onNextWeekClick() {
        selectedDate.update { it.plusWeeks(1) }
    }

    fun onPreviousWeekClick() {
        selectedDate.update { it.minusWeeks(1) }
    }

    fun getDateByDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
        return selectedDate.value.plusDays(dayOfWeek.ordinal.toLong())
    }
}

class TimetableOwnerDelegate(
    componentContext: ComponentContext,
    initialDate: LocalDate = LocalDate.now(),
) : TimetableOwnerComponent {
    override val componentScope = componentContext.componentScope()
    override val selectedDate = MutableStateFlow(initialDate)

    override val mondayDate: StateFlow<LocalDate> = this.selectedDate.map(componentScope) {
        it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

//    init {
//        componentScope.launch {
//            mondayDate.collect {
//                if (it.dayOfWeek != DayOfWeek.MONDAY)
//                    throw IllegalStateException("dayOfWeek should only be Monday")
//            }
//        }
//    }

    override val selectedWeekOfYear = selectedDate.map(componentScope) {
        val week = it.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = it.get(WeekFields.ISO.weekBasedYear())
        "${year}_${week}".apply { println("WEEK owner: $this") }
    }

}