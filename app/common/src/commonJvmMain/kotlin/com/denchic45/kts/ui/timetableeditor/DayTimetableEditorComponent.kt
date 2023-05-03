package com.denchic45.kts.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.domain.usecase.FindTimetableOfWeekUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.copy
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class DayTimetableEditorComponent constructor(
    metaRepository: MetaRepository,
    private val dayTimetableComponentDelegate: (
        _selectedDate: LocalDate,
        owner: Flow<TimetableOwner>,
        componentContext: ComponentContext
    ) -> DayTimetableComponentDelegate,
    findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
//    @Assisted
//    weekOfYear: String,
    @Assisted
    owner: Flow<TimetableOwner.StudyGroup>,
//    @Assisted
//    private val weekTimetableFlows: List<MutableStateFlow<List<PeriodResponse>>>,
    @Assisted
    private val isEdit: Flow<Boolean> = flowOf(false),
    @Assisted
    private val _selectedDate: LocalDate,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val dayTimetableComponent = dayTimetableComponentDelegate(
        _selectedDate,
        owner,
        componentContext.childContext("dayTimetable")
    )

//    private val selectedDay = selectedDate.map { it.dayOfWeek.ordinal }.stateIn(
//        componentScope,
//        SharingStarted.Lazily,
//        selectedDate.value.dayOfWeek.ordinal
//    )

    private val editingWeekTimetable: List<MutableStateFlow<List<PeriodResponse>>> = listOf(
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
    )


//    private val bellSchedule = metaRepository.observeBellSchedule
//        .shareIn(componentScope, SharingStarted.Lazily)

//    @OptIn(FlowPreview::class)
//    val viewState = combine(selectedDay, bellSchedule, isEdit) { selected, schedule, isEdit ->
//        weekTimetableFlows[selected].map {
//            resourceOf(
//                it.toTimetableViewState(
//                    date = mondayDate.plusDays(selected.toLong()),
//                    bellSchedule = schedule,
//                    isEdit = isEdit
//                )
//            )
//        }
//    }.flattenConcat().stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)

    init {
        componentScope().launch {
            val flatMapLatest: Flow<Resource<DayTimetableViewState?>> = isEdit.flatMapLatest {
                if (it) {
                    dayTimetableComponent.observeTimetableFlow
                } else {
                    dayTimetableComponent.bellSchedule.flatMapLatest { schedule ->
                        dayTimetableComponent.selectedWeekOfYear.flatMapLatest { weekOfYear ->
                            dayTimetableComponent.selectedDate.flatMapLatest { selectedDate ->
                                editingWeekTimetable[selectedDate.dayOfWeek.ordinal].flatMapLatest { periods ->
                                    flowOf(
                                        resourceOf(
                                            DayTimetableViewState.create(
                                                schedule,
                                                selectedDate,
                                                periods
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }

                }
            }
            dayTimetableComponent.viewStateSource.emit(flatMapLatest)
        }
    }

    fun onAddPeriod(period: PeriodResponse) {
        weekTimetableFlows[selectedDay.value].update { it + period }
    }

    fun onUpdatePeriod(position: Int, period: PeriodResponse) {
        weekTimetableFlows[selectedDay.value].update {
            it.copy {
                this[position] = period
            }
        }
    }

    fun onRemovePeriod(position: Int) {
        weekTimetableFlows[selectedDay.value].update {
            it - it[position]
        }
    }

    fun onPeriodEdit(position: Int) {
        TODO("Not yet implemented")
    }
}