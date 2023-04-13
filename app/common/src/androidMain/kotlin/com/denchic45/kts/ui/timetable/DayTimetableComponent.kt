package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindTimetableOfWeekUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner2
import com.denchic45.kts.ui.ToolbarInteractor
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.capitalized
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.DatePatterns
import com.denchic45.stuiversity.util.Dates
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.*


@Inject
class DayTimetableComponent(
    metaRepository: MetaRepository,
    private val toolbarInteractor: ToolbarInteractor,
    private val findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
    @Assisted
    private val _selectedDate: LocalDate,
    @Assisted
    private val owner: Flow<TimetableOwner2>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val selectedDate = MutableStateFlow(_selectedDate)
    private val selectedWeekOfYear = selectedDate.map(componentScope) {
        it.toString(DatePatterns.YYYY_ww)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val timetable = owner.flatMapLatest { owner ->
        selectedWeekOfYear.flatMapLatest { weekOfYear ->
            flow {
                emit(Resource.Loading)
                emit(findTimetableOfWeekUseCase(weekOfYear, owner))
            }
        }
    }

    private val bellSchedule = metaRepository.observeBellSchedule

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState = bellSchedule.flatMapLatest { schedule ->
        selectedWeekOfYear.flatMapLatest { weekOfYear ->
            timetable.flatMapLatest { timetableResource ->
                getTimetableOfSelectedDateFlow(weekOfYear, timetableResource, schedule)
            }
        }
    }.stateInResource(componentScope)

    init {
        lifecycle.subscribe(
            onCreate = { println("LIFECYCLE TIMETABLE: create") },
            onStart = {
                println("LIFECYCLE TIMETABLE: start")
                selectedDate.onEach { selected ->
                    toolbarInteractor.title =
                        uiTextOf(Dates.toStringHidingCurrentYear(selected).capitalized())
                }.launchIn(componentScope)
            },
            onResume = {
                println("LIFECYCLE TIMETABLE: resume")

            },
            onPause = { println("LIFECYCLE TIMETABLE: pause") },
            onStop = { println("LIFECYCLE TIMETABLE: stop") },
            onDestroy = { println("LIFECYCLE TIMETABLE: destroy") }
        )
    }

    private fun getTimetableOfSelectedDateFlow(
        weekOfYear: String,
        timetableResource: Resource<TimetableResponse>,
        schedule: BellSchedule,
    ) = selectedDate.filter { it.toString(DatePatterns.YYYY_ww) == weekOfYear }
        .map { selected ->
            timetableResource.map {
                val selectedDay = selected.dayOfWeek.ordinal
                if (selectedDay == 6) null
                else it.days[selectedDay].toTimetableViewState(
                    date = selected,
                    bellSchedule = schedule
                )
            }
        }

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    fun onTodayClick() {
        selectedDate.value = LocalDate.now()
    }

}