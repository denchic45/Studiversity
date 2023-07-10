package com.denchic45.studiversity.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.map
import com.denchic45.stuiversity.util.withDayOfWeek
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields

interface TimetableOwnerComponent {
    val mondayDate: MutableStateFlow<LocalDate>
    val selectedWeekOfYear: StateFlow<String>
    val componentScope: CoroutineScope

    fun onWeekSelect(monday: LocalDate) {
        mondayDate.value = monday
    }

    fun onTodayClick() = mondayDate.update {
        LocalDate.now().withDayOfWeek(DayOfWeek.MONDAY)
    }

    fun onNextWeekClick() {
        mondayDate.update { it.plusWeeks(1) }
    }

    fun onPreviousWeekClick() {
        mondayDate.update { it.minusWeeks(1) }
    }

    fun getDateByDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
        return mondayDate.value.plusDays(dayOfWeek.ordinal.toLong())
    }
}

class TimetableOwnerDelegate(
    componentContext: ComponentContext,
    initialDate: LocalDate = LocalDate.now().withDayOfWeek(DayOfWeek.MONDAY),
) : TimetableOwnerComponent {
    override val mondayDate = MutableStateFlow(initialDate)
    override val componentScope = componentContext.componentScope()

    init {
        componentScope.launch {
            mondayDate.collect {
                if (it.dayOfWeek != DayOfWeek.MONDAY)
                    throw IllegalStateException("dayOfWeek should only be Monday")
            }
        }
    }

    override val selectedWeekOfYear = mondayDate.map(componentScope) {
        val week = it.get(WeekFields.ISO.weekOfWeekBasedYear())
        val year = it.get(WeekFields.ISO.weekBasedYear())
        "${year}_${week}".apply { println("WEEK owner: $this") }
    }

//    override val mondayDate: StateFlow<LocalDate> = this.mondayDate.map(componentScope) {
//        it.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
//    }
}