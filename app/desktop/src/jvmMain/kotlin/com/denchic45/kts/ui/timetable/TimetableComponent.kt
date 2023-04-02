package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourTimetableByUseCase
import com.denchic45.kts.util.componentScope
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
            timetable.toTimetableViewState(bellSchedule)
        }
    }.stateInResource(coroutineScope)


    fun onNextWeekClick() = selectedDate.update { it.plusWeeks(1) }

    fun onPreviousWeekClick() = selectedDate.update { it.minusWeeks(1) }

    fun onTodayClick() = selectedDate.update { currentWeek }
}