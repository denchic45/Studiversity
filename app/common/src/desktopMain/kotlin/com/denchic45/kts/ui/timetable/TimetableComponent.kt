//package com.denchic45.kts.ui.timetable
//
//import com.arkivanov.decompose.ComponentContext
//import com.arkivanov.essenty.lifecycle.subscribe
//import com.denchic45.kts.data.repository.MetaRepository
//import com.denchic45.kts.domain.map
//import com.denchic45.kts.domain.stateInResource
//import com.denchic45.kts.domain.usecase.FindYourTimetableByUseCase
//import com.denchic45.kts.ui.timetable.state.toDayTimetableViewState
//import com.denchic45.kts.util.componentScope
//import com.denchic45.stuiversity.util.DateTimePatterns
//import com.denchic45.stuiversity.util.toString
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.*
//import me.tatarka.inject.annotations.Assisted
//import me.tatarka.inject.annotations.Inject
//import java.time.DayOfWeek
//import java.time.LocalDate
//
//@Inject
//class TimetableComponent(
//    findYourTimetableByUseCase: FindYourTimetableByUseCase,
//    metaRepository: MetaRepository,
//    @Assisted
//    componentContext: ComponentContext,
//) : ComponentContext by componentContext {
//
//    private val coroutineScope = componentScope()
//
//    private val currentWeek: LocalDate
//        get() = LocalDate.now().with(DayOfWeek.MONDAY)
//
//    val selectedDate = MutableStateFlow(currentWeek)
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val viewState = combine(
//        selectedDate.mapLatest { date ->
//            findYourTimetableByUseCase(date.toString(DateTimePatterns.YYYY_ww))
//        },
//        metaRepository.observeBellSchedule
//    ) { timetableResource, bellSchedule ->
//        timetableResource.map { timetable ->
//            timetable.days.toDayTimetableViewState(currentWeek.toString(DateTimePatterns.YYYY_ww),bellSchedule)
//        }
//    }.stateInResource(coroutineScope)
//
//    init {
//        lifecycle.subscribe(
//            onCreate = { println("LIFECYCLE TIMETABLE: create") },
//            onStart = { println("LIFECYCLE TIMETABLE: start") },
//            onResume = { println("LIFECYCLE TIMETABLE: resume") },
//            onPause = { println("LIFECYCLE TIMETABLE: pause") },
//            onStop = { println("LIFECYCLE TIMETABLE: stop") },
//            onDestroy = { println("LIFECYCLE TIMETABLE: destroy") }
//        )
//    }
//
//    fun onNextWeekClick() = selectedDate.update { it.plusWeeks(1) }
//
//    fun onPreviousWeekClick() = selectedDate.update { it.minusWeeks(1) }
//
//    fun onTodayClick() = selectedDate.update { currentWeek }
//}