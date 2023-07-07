package com.denchic45.studiversity.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.timetable.model.EventRequest
import com.denchic45.stuiversity.api.timetable.model.EventResponse
import com.denchic45.stuiversity.api.timetable.model.LessonRequest
import com.denchic45.stuiversity.api.timetable.model.LessonResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class DayTimetableEditorComponent(
    @Assisted
    private val source: TimetableState,
    @Assisted
    private val studyGroupId: UUID,
//    @Assisted
//    private val _selectedDate: StateFlow<LocalDate>,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

//    private val selectedDay = _selectedDate.map(componentScope) { it.dayOfWeek.ordinal }

//    val editingWeekTimetable = MutableStateFlow<List<List<PeriodResponse>>>(
//        listOf(
//            emptyList(),
//            emptyList(),
//            emptyList(),
//            emptyList(),
//            emptyList(),
//            emptyList(),
//        )
//    )

    val editingTimetableState = MutableStateFlow(source)

//    init {
//        componentScope.launch {
//            source.let { response ->
//                editingWeekTimetable.value = response.days
//            }
//        }
//    }


//    @OptIn(ExperimentalCoroutinesApi::class)
//    val viewState = bellSchedule.flatMapLatest { schedule ->
//        mondayDate.flatMapResourceFlow { monday ->
//            isEdit.flatMapLatest { edit ->
//                selectedDay.flatMapLatest { selected ->
//                    if (edit) {
//                        editingWeekTimetable[selected].map {
//                            resourceOf(
//                                it.toTimetableViewState(
//                                    date = monday.plusDays(selected.toLong()),
//                                    bellSchedule = schedule,
//                                    isEdit = true
//                                )
//                            )
//                        }
//                    } else {
//                        sourceFlow.mapResource {
//                            it.days[selected].toTimetableViewState(
//                                date = monday.plusDays(selected.toLong()),
//                                bellSchedule = schedule,
//                                isEdit = false
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }.shareIn(componentScope, SharingStarted.Lazily, 1)

    fun onAddPeriod(dayOfWeek: Int, period: PeriodResponse) {
        editingTimetableState.update { timetable ->
            timetable.addPeriod(dayOfWeek, period)
        }
    }

    fun onUpdatePeriod(dayOfWeek: Int, position: Int, period: PeriodResponse) {
        editingTimetableState.update { timetable ->
            timetable.updatePeriod(dayOfWeek, period, position)
        }
    }

    fun onRemovePeriod(dayOfWeek: Int, position: Int) {
        editingTimetableState.update { timetable ->
            timetable.removePeriod(dayOfWeek, position)
        }
    }

    private fun PeriodResponse.updateOrder(order: Int) = when (this) {
        is EventResponse -> copy(order = order)
        is LessonResponse -> copy(order = order)
    }

    fun onDestroy() {}


    val request: PutTimetableRequest
        get() = editingTimetableState.value.timetable.map {
            it.map { response ->
                when (response) {
                    is EventResponse -> EventRequest(
                        order = response.order,
                        roomId = response.room?.id,
                        memberIds = response.members.map(PeriodMember::id),
                        name = response.details.name,
                        color = response.details.color,
                        iconUrl = response.details.iconUrl
                    )

                    is LessonResponse -> LessonRequest(
                        order = response.order,
                        roomId = response.room?.id,
                        memberIds = response.members.map(PeriodMember::id),
                        courseId = response.details.course.id
                    )
                }
            }
        }.let {
            PutTimetableRequest(
                studyGroupId = studyGroupId,
                monday = it[0],
                tuesday = it[1],
                wednesday = it[2],
                thursday = it[3],
                friday = it[4],
                saturday = it[5]
            )
        }

    fun TimetableState.toPutTimetableRequest() {

        fun List<PeriodSlot>.toPeriodRequests() {
            buildList {
                this@toPeriodRequests.withIndex()
                    .mapNotNull { (index, item) -> (item as? PeriodItem)?.let { index to it } }
                    .map { (index, item) ->
                        when (item.details) {
                            is PeriodDetails.Lesson -> {

                            }
                            is PeriodDetails.Event -> {

                            }
                        }
                    }
            }
        }

        return buildList {
            timetable.map(List<PeriodSlot>::toPeriodRequests)
        }
    }


}