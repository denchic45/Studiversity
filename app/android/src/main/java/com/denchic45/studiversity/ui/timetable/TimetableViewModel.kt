package com.denchic45.studiversity.ui.timetable

//import androidx.lifecycle.viewModelScope
//import com.denchic45.studiversity.common.R
//import com.denchic45.studiversity.domain.mapResource
//import com.denchic45.studiversity.domain.model.Event
//import com.denchic45.studiversity.domain.model.User
//import com.denchic45.studiversity.domain.usecase.FindYourTimetableOfDayUseCase
//import com.denchic45.studiversity.ui.base.BaseViewModel
//import com.denchic45.studiversity.util.capitalized
//import com.denchic45.stuiversity.util.Dates
//import com.denchic45.studiversity.ui.widget.calendar.model.WeekItem
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import java.time.DayOfWeek
//import java.time.LocalDate
//import javax.inject.Inject
//import javax.inject.Named
//
//class TimetableViewModel @Inject constructor(
//    @Named(TimetableFragment.GROUP_ID) receivedGroupId: String?,
//    private val interactor: TimetableInteractor,
//    findYourTimetableOfDayUseCase: FindYourTimetableOfDayUseCase
//) : BaseViewModel() {
//    val initTimetable = MutableSharedFlow<Boolean>(replay = 1)
//    val events: StateFlow<EventsState>
//    private val groupId: Flow<String>
//    var selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
//
//    fun onWeekSelect(weekItem: WeekItem) {
//        val selectedDay = weekItem.selectedDay
//        toolbarTitle = if (selectedDay == -1) {
//            Dates.toStringHidingCurrentYear(weekItem[3]).capitalized()
//        } else {
//            Dates.toStringHidingCurrentYear(weekItem[selectedDay]).capitalized()
//        }
//    }
//
//    fun onWeekLoad(weekItem: WeekItem) {
//        //Nothing
//    }
//
//    fun onDaySelect(date: LocalDate) {
//        viewModelScope.launch {
//            selectedDate.emit(date)
//            toolbarTitle = Dates.toStringHidingCurrentYear(date).capitalized()
//        }
//    }
//
//    val lessonTime: Int
//        get() = interactor.lessonTime
//
//    override fun onCleared() {
//        super.onCleared()
//        interactor.removeListeners()
//    }
//
//    init {
//        val role = interactor.role
//        groupId = if (receivedGroupId == null) {
//            interactor.observeYourGroupId().shareIn(viewModelScope, SharingStarted.Lazily)
//        } else {
//            flowOf(receivedGroupId)
//        }
//
//        viewModelScope.launch { initTimetable.emit(User.isTeacher(role)) }
//
//        events = selectedDate.flatMapLatest { selectedDate ->
//            when {
//                selectedDate.dayOfWeek == DayOfWeek.SUNDAY -> flowOf(EventsState.DayOff)
//                receivedGroupId != null -> interactor.findEventsOfGroupByDate(
//                    selectedDate,
//                    receivedGroupId
//                )
//                    .map { EventsState.Events(it.events) }
//                else -> flowOf(findYourTimetableOfDayUseCase(selectedDate)).mapResource { EventsState.Events(it.events) }
//            }
//        }.stateIn(viewModelScope, SharingStarted.Lazily, EventsState.Events(emptyList()))
//
//        toolbarTitle = Dates.toStringHidingCurrentYear(LocalDate.now()).capitalized()
//    }
//
//    sealed class EventsState {
//        data class Events(
//            val events: List<Event>
//        ) : EventsState()
//
//        object DayOff : EventsState()
//    }
//
//    override fun onOptionClick(itemId: Int) {
//        when (itemId) {
//            R.id.option_select_today -> {
//                viewModelScope.launch {
//                    selectedDate.emit(LocalDate.now())
//                }
//            }
//        }
//    }
//}