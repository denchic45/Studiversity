package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindTimetableOfWeekUseCase
import com.denchic45.kts.domain.usecase.FindYourTimetableOfWeekUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.util.DatePatterns
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
    private val findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
    private val findYourTimetableOfWeekUseCase: FindYourTimetableOfWeekUseCase,
    @Assisted
    private val _selectedDate: LocalDate,
    @Assisted
    private val owner: TimetableOwner,
    @Assisted
    private val ownerId: UUID?,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()


    private val selectedDate = MutableStateFlow(_selectedDate)
    private val selectedWeekOfYear = selectedDate.map { it.toString(DatePatterns.YYYY_ww) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val timetable = selectedWeekOfYear.flatMapLatest { weekOfYear ->
        flow {
            emit(
                if (owner == TimetableOwner.Member && ownerId == null)
                    findYourTimetableOfWeekUseCase(weekOfYear)
                else
                    findTimetableOfWeekUseCase(weekOfYear, owner, ownerId!!)
            )
        }
    }

//    private val selectedDay = MutableStateFlow(0)

    private val bellSchedule = metaRepository.observeBellSchedule

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState = bellSchedule.flatMapLatest { schedule ->
        timetable.flatMapLatest { timetableResource ->
            selectedDate.map { selected ->
                timetableResource.map {
                    it.days[selected.dayOfWeek.value].toTimetableViewState(
                        date = selected,
                        bellSchedule = schedule
                    )
                }
            }
        }
    }.stateInResource(componentScope)

//        @OptIn(FlowPreview::class)
//        val viewState = combine(selectedDay, bellSchedule) { selected, schedule ->
//            timetable.mapResource {
//                it.days[selected].toTimetableViewState(
//                    date = selectedWeekOfYear.value.plusDays(selected.toLong()),
//                    bellSchedule = schedule
//                )
//            }
//        }.flattenConcat().stateIn(componentScope, SharingStarted.Lazily, null)

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date

    }

    fun onTodayClick() {

    }

}