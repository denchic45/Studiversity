package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourTimetableByUseCase
import com.denchic45.kts.util.capitalized
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.timetable.model.EventDetails
import com.denchic45.stuiversity.api.timetable.model.LessonDetails
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.DatePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.max

@Inject
class TimetableComponent(
    findYourTimetableByUseCase: FindYourTimetableByUseCase,
    metaRepository: MetaRepository,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val coroutineScope = componentScope()

    private val currentWeek: LocalDate
        get() = LocalDate.now().with(DayOfWeek.MONDAY)

    private val selectedDate = MutableStateFlow(currentWeek)

    @OptIn(ExperimentalCoroutinesApi::class)
    val timetable = combine(
        selectedDate.mapLatest { date ->
            findYourTimetableByUseCase(date.toString(DatePatterns.YYYY_ww))
        },
        metaRepository.observeBellSchedule
    ) { timetableResource, bellSchedule ->
        timetableResource.map { timetable ->
            val latestEventOrder = max(timetable.days.maxOf { it.last().order }, 6)

            TimetableViewState(
                mondayDate = selectedDate.value,
                periods = toCellItems(timetable, latestEventOrder),
                orders = buildList {
                    bellSchedule.schedule.take(latestEventOrder)
                        .forEachIndexed { index, period ->
                            add(TimetableViewState.CellOrder(index + 1, period.first))
                        }
                },
                maxEventsSize = latestEventOrder
            )
        }
    }.stateInResource(coroutineScope)

    private fun toCellItems(
        timetable: TimetableResponse,
        latestPeriodOrder: Int
    ): List<List<TimetableViewState.Cell>> {
        return buildList {
            var lastOrder = 0
            timetable.days.forEach { periods ->
                add(buildList {
                    periods.forEach { period ->
                        val diffOrders = period.order - lastOrder
                        if (diffOrders > 1) {
                            repeat(diffOrders) { add(TimetableViewState.Cell.Empty) }
                        } else {
                            add(
                                when (val details = period.details) {
                                    is LessonDetails -> TimetableViewState.Cell.Event(
                                        details.subject.iconName,
                                        details.subject.name,
                                        period.room
                                    )
                                    is EventDetails -> TimetableViewState.Cell.Event(
                                        details.iconUrl,
                                        details.name,
                                        period.room
                                    )
                                }
                            )
                        }
                        lastOrder++
                    }
                })
            }
        }
    }

    fun onNextWeekClick() = selectedDate.update { it.plusWeeks(1) }

    fun onPreviousWeekClick() = selectedDate.update { it.minusWeeks(1) }

    fun onTodayClick() = selectedDate.update { currentWeek }
}

data class TimetableViewState(
    val mondayDate: LocalDate,
    val periods: List<List<Cell>>,
    val orders: List<CellOrder>,
    val maxEventsSize: Int,
) {

    val title = getMonthTitle(mondayDate)

    private fun getMonthTitle(monday: LocalDate): String {
        val saturday: LocalDate = monday.plusDays(5)
        return if (monday.monthValue != saturday.monthValue) {
            if (monday.year != saturday.year) {

                "${monday.toString("LLL yy").capitalized().replace(".", "")} - ${
                    saturday.toString("LLL yy").replace(".", "")
                }"
            } else {
                "${
                    (monday.toString("LLL").replace(".", "")).capitalized()
                } - ${saturday.toString("LLL").replace(".", "")}"
            }
        } else {
            monday.toString("LLLL").capitalized()
        }
    }

    sealed class Cell {
        data class Event(val iconName: String, val name: String, val room: RoomResponse?) : Cell()
        object Empty : Cell()
    }

    data class CellOrder(val order: Int, val time: String)
}