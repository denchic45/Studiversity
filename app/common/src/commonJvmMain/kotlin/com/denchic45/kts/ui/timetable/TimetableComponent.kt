package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindTimetableOfWeekUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.util.capitalized
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.util.toDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*


@Inject
class TimetableComponent(
    private val findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
    @Assisted val selectedWeekOfYear: StateFlow<String>,
    @Assisted
    private val owner: Flow<TimetableOwner>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _weekTimetable = owner.flatMapLatest { owner ->
        selectedWeekOfYear.map { weekOfYear ->
//            flow {
//                emit(Resource.Loading)
//                emit(
            findTimetableOfWeekUseCase(weekOfYear, owner)
//                )
//            }
        }
    }

    val weekTimetable = _weekTimetable.stateInResource(componentScope)
}

private fun LocalDate.getMonthName(): String {
    return SimpleDateFormat("LLLL").format(toDate()).capitalized()
}

private fun LocalDate.getMonthWithYear(): String {
    return getMonthName() + year
}

fun getMonthTitle(yearWeek: String): String {
    val monday = LocalDate.parse(
        yearWeek, DateTimeFormatterBuilder()
            .appendPattern("YYYY_ww")
            .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            .toFormatter()
    )
    val saturday: LocalDate = monday.plusDays(5)
    return if (monday.monthValue != saturday.monthValue) {
        if (monday.year != saturday.year) {
            "${monday.getMonthWithYear()} - ${saturday.getMonthWithYear()}"
        } else {
            "${monday.getMonthName()} - ${saturday.getMonthName()}"
        }
    } else {
        monday.getMonthName()
    }
}