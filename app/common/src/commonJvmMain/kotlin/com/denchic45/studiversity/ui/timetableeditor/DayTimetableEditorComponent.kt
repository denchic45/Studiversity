package com.denchic45.studiversity.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.copy
import com.denchic45.studiversity.util.map
import com.denchic45.stuiversity.api.timetable.model.EventRequest
import com.denchic45.stuiversity.api.timetable.model.EventResponse
import com.denchic45.stuiversity.api.timetable.model.LessonRequest
import com.denchic45.stuiversity.api.timetable.model.LessonResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class DayTimetableEditorComponent(
//    metaRepository: MetaRepository,
    @Assisted
    private val source: TimetableResponse,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    private val _selectedDate: StateFlow<LocalDate>,
//    @Assisted
//    private val owner: Flow<TimetableOwner>,
//    @Assisted
//    private val _weekTimetable: List<List<PeriodResponse>>,
//    @Assisted
//    private val onFinish: (PutTimetableRequest?) -> Unit,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

//    val selectedDate = MutableStateFlow(_selectedDate)

//    private val sourceFlow = _sourceFlow.shareIn(componentScope, SharingStarted.Lazily)

//    private val mondayDate = sourceFlow.mapResource { it.weekOfYear.toLocalDateOfWeekOfYear() }

    private val selectedDay = _selectedDate.map(componentScope) { it.dayOfWeek.ordinal }

//    val editingWeekTimetable: List<MutableStateFlow<List<PeriodResponse>>> = listOf(
//        MutableStateFlow(emptyList()),
//        MutableStateFlow(emptyList()),
//        MutableStateFlow(emptyList()),
//        MutableStateFlow(emptyList()),
//        MutableStateFlow(emptyList()),
//        MutableStateFlow(emptyList()),
//    )

    val editingWeekTimetable = MutableStateFlow<List<List<PeriodResponse>>>(
        listOf(
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
            emptyList(),
        )
    )

    init {
        componentScope.launch {
            source.let { response ->
                editingWeekTimetable.value = response.days
            }
        }
    }


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

    fun onAddPeriod(period: PeriodResponse) {
        editingWeekTimetable.update {
            it.copy {
                this[selectedDay.value] = this[selectedDay.value] + period
            }
        }
    }

    fun onUpdatePeriod(position: Int, period: PeriodResponse) {
        editingWeekTimetable.update {
            it.copy {
                this[selectedDay.value] = this[selectedDay.value].copy {
                    this[position] = period
                }
            }
        }
    }

    fun onPeriodRemove(position: Int) {
        editingWeekTimetable.update { timetable ->
            timetable.copy {
                this[selectedDay.value] = this[selectedDay.value].copy {
                    removeAt(position)
                    val diff = size - position
                    if (diff > 0)
                        repeat(diff) {
                            val response = this[position + it]
                            this[position + it] = response.updateOrder(response.order - 1)
                        }
                }
            }
        }
    }

    private fun PeriodResponse.updateOrder(order: Int) = when (this) {
        is EventResponse -> copy(order = order)
        is LessonResponse -> copy(order = order)
    }


    val request: PutTimetableRequest
        get() = editingWeekTimetable.value.map {
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
}