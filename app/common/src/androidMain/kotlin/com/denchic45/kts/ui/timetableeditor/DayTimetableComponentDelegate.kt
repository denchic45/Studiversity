package com.denchic45.kts.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindTimetableOfWeekUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import java.time.LocalDate


class DayTimetableComponentDelegate(
    metaRepository: MetaRepository,
    private val findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
    @Assisted
    private val _selectedDate: LocalDate,
    @Assisted
    private val owner: Flow<TimetableOwner>,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    protected val componentScope = componentScope()

    val selectedDate = MutableStateFlow(_selectedDate)

    val selectedWeekOfYear = selectedDate.map(componentScope) {
        it.toString(DateTimePatterns.YYYY_ww)
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//     val foundWeekTimetable = owner.flatMapLatest { owner ->
//        selectedWeekOfYear.flatMapLatest { weekOfYear ->
//            flow {
//                emit(Resource.Loading)
//                emit(findTimetableOfWeekUseCase(weekOfYear, owner))
//            }
//        }
//    }.shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(ExperimentalCoroutinesApi::class)
    val weekTimetable = owner.flatMapLatest { owner ->
        selectedWeekOfYear.flatMapLatest { weekOfYear ->
            flow {
                emit(Resource.Loading)
                emit(findTimetableOfWeekUseCase(weekOfYear, owner))
            }
        }
    }.shareIn(componentScope, SharingStarted.Lazily)

//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val mapToViewState = _mapToViewState ?:

    val bellSchedule = metaRepository.observeBellSchedule

    @OptIn(ExperimentalCoroutinesApi::class)
    val observeTimetableFlow = bellSchedule.flatMapLatest { schedule ->
        selectedWeekOfYear.flatMapLatest { weekOfYear ->
            weekTimetable.flatMapLatest { timetableResource ->
                createTimetableViewState(weekOfYear, timetableResource, schedule)
            }
        }
    }

    val viewStateSource = MutableStateFlow<Flow<Resource<DayTimetableViewState?>>>(flowOf(resourceOf()))

    @OptIn(FlowPreview::class)
    val viewState = viewStateSource.flattenConcat().stateInResource(componentScope)

    private fun createTimetableViewState(
        weekOfYear: String,
        timetableResource: Resource<TimetableResponse>,
        schedule: BellSchedule,
    ) = selectedDate.filter { it.toString(DateTimePatterns.YYYY_ww) == weekOfYear }
        .map { selected ->
            timetableResource.map { timetable ->
                DayTimetableViewState.create(
                    schedule = schedule,
                    selected = selected,
                    periods = timetable.days[selected.dayOfWeek.ordinal]
                )
            }
        }

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }
}