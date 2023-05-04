package com.denchic45.kts.ui.timetable

//import com.arkivanov.decompose.ComponentContext
//import com.arkivanov.essenty.lifecycle.doOnStart
//import com.denchic45.kts.R
//import com.denchic45.kts.data.repository.MetaRepository
//import com.denchic45.kts.data.service.model.BellSchedule
//import com.denchic45.kts.domain.Resource
//import com.denchic45.kts.domain.map
//import com.denchic45.kts.domain.stateInResource
//import com.denchic45.kts.domain.usecase.FindTimetableOfWeekUseCase
//import com.denchic45.kts.domain.usecase.TimetableOwner
//import com.denchic45.kts.ui.ActionMenuItem
//import com.denchic45.kts.ui.appbar.AppBarInteractor
//import com.denchic45.kts.ui.appbar.AppBarState
//import com.denchic45.kts.ui.timetable.state.toTimetableViewState
//import com.denchic45.kts.ui.uiIconOf
//import com.denchic45.kts.ui.uiTextOf
//import com.denchic45.kts.util.capitalized
//import com.denchic45.kts.util.componentScope
//import com.denchic45.kts.util.map
//import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
//import com.denchic45.stuiversity.util.DateTimePatterns
//import com.denchic45.stuiversity.util.Dates
//import com.denchic45.stuiversity.util.toString
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.*
//import me.tatarka.inject.annotations.Assisted
//import me.tatarka.inject.annotations.Inject
//import java.time.LocalDate
//import java.util.*
//
//
//@Inject
//class DayTimetableComponent(
//    metaRepository: MetaRepository,
//    private val appBarInteractor: AppBarInteractor,
//    private val findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
//    @Assisted
//    private val _selectedDate: LocalDate,
//    @Assisted
//    private val owner: Flow<TimetableOwner>,
//    @Assisted
//    componentContext: ComponentContext,
//) : ComponentContext by componentContext {
//    private val componentScope = componentScope()
//
//    val selectedDate = MutableStateFlow(_selectedDate)
//
//    private val _selectedWeekOfYear = selectedDate.map(componentScope) {
//        it.toString(DateTimePatterns.YYYY_ww)
//    }
//
//     val selectedWeekOfYear = _selectedWeekOfYear
//        .shareIn(componentScope, SharingStarted.Lazily, 1)
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val _weekTimetable = owner.flatMapLatest { owner ->
//        _selectedWeekOfYear.flatMapLatest { weekOfYear ->
//            flow {
//                emit(Resource.Loading)
//                emit(findTimetableOfWeekUseCase(weekOfYear, owner))
//            }
//        }
//    }
//
//    val weekTimetable = _weekTimetable.stateInResource(componentScope)
//
//    private val bellSchedule = metaRepository.observeBellSchedule
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val viewState = bellSchedule.flatMapLatest { schedule ->
//        _selectedWeekOfYear.flatMapLatest { weekOfYear ->
//            _weekTimetable.flatMapLatest { timetableResource ->
//                getTimetableOfSelectedDateFlow(weekOfYear, timetableResource, schedule)
//            }
//        }
//    }.stateInResource(componentScope)
//
//    init {
//        lifecycle.doOnStart {
//            appBarInteractor.set(AppBarState(
//                actions = listOf(
//                    ActionMenuItem(
//                        "today",
//                        uiIconOf(R.drawable.ic_calendar)
//                    ) { selectedDate.value = LocalDate.now() }
//                )
//            ))
//            selectedDate.onEach { selected ->
//                appBarInteractor.update {
//                    it.copy(
//                        title = uiTextOf(
//                            Dates.toStringMonthHidingCurrentYear(selected).capitalized()
//                        )
//                    )
//                }
//            }.launchIn(componentScope)
//        }
//    }
//
//    private fun getTimetableOfSelectedDateFlow(
//        weekOfYear: String,
//        timetableResource: Resource<TimetableResponse>,
//        schedule: BellSchedule,
//    ) = selectedDate.filter { it.toString(DateTimePatterns.YYYY_ww) == weekOfYear }
//        .map { selected ->
//            timetableResource.map {
//                val selectedDay = selected.dayOfWeek.ordinal
//                if (selectedDay == 6) null
//                else it.days[selectedDay].toTimetableViewState(
//                    date = selected,
//                    bellSchedule = schedule
//                )
//            }
//        }
//
//    fun onDateSelect(date: LocalDate) {
//        selectedDate.value = date
//    }
//
//    fun onTodayClick() {
//        selectedDate.value = LocalDate.now()
//    }
//}