package com.denchic45.kts.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResourceFlow
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.ui.timetable.state.toLocalDateOfWeekOfYear
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.copy
import com.denchic45.stuiversity.api.timetable.model.EventRequest
import com.denchic45.stuiversity.api.timetable.model.EventResponse
import com.denchic45.stuiversity.api.timetable.model.LessonRequest
import com.denchic45.stuiversity.api.timetable.model.LessonResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class DayTimetableEditorComponent(
    metaRepository: MetaRepository,
    @Assisted
    private val _sourceFlow: Flow<Resource<TimetableResponse>>,
    @Assisted
    private val studyGroupId: UUID,
//    @Assisted
//    private val _selectedDate: LocalDate,
//    @Assisted
//    private val owner: Flow<TimetableOwner>,
//    @Assisted
//    private val _weekTimetable: List<List<PeriodResponse>>,
    @Assisted
    private val onFinish: (PutTimetableRequest?) -> Unit,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    //    val selectedDate = MutableStateFlow(_selectedDate)
    private val isEdit = MutableStateFlow(false)

    private val sourceFlow = _sourceFlow.shareIn(componentScope, SharingStarted.Lazily)

    private val mondayDate = sourceFlow.mapResource { it.weekOfYear.toLocalDateOfWeekOfYear() }

    private val selectedDay = MutableStateFlow(0)

    private val editingWeekTimetable: List<MutableStateFlow<List<PeriodResponse>>> = listOf(
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
        MutableStateFlow(emptyList()),
    )

    init {
        componentScope.launch {
            sourceFlow.filter { isEdit.value }.collect {
                it.onSuccess { response ->
                    editingWeekTimetable[0].value = response.monday
                    editingWeekTimetable[1].value = response.tuesday
                    editingWeekTimetable[2].value = response.wednesday
                    editingWeekTimetable[3].value = response.thursday
                    editingWeekTimetable[4].value = response.friday
                    editingWeekTimetable[5].value = response.saturday
                }
            }
        }
    }


    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewState = bellSchedule.flatMapLatest { schedule ->
        mondayDate.flatMapResourceFlow { monday ->
            isEdit.flatMapLatest { edit ->
                selectedDay.flatMapLatest { selected ->
                    if (edit) {
                        editingWeekTimetable[selected].map {
                            resourceOf(
                                it.toTimetableViewState(
                                    date = monday.plusDays(selected.toLong()),
                                    bellSchedule = schedule,
                                    isEdit = true
                                )
                            )
                        }
                    } else {
                        sourceFlow.mapResource {
                            it.days[selected].toTimetableViewState(
                                date = monday.plusDays(selected.toLong()),
                                bellSchedule = schedule,
                                isEdit = false
                            )
                        }
                    }
                }
            }
        }
    }.shareIn(componentScope, SharingStarted.Lazily, 1)

    fun onAddPeriod(period: PeriodResponse) {
        editingWeekTimetable[selectedDay.value].update { it + period }
    }

    fun onPeriodUpdate(position: Int, period: PeriodResponse) {
        editingWeekTimetable[selectedDay.value].update {
            it.copy {
                this[position] = period
            }
        }
    }

    fun onPeriodRemove(position: Int) {
        editingWeekTimetable[selectedDay.value].update {
            it - it[position]
        }
    }

    fun onPeriodEdit(position: Int) {
        TODO("Not yet implemented")
    }

    fun onSaveClick() {
        onFinish(editingWeekTimetable.map {

            it.value.map { response ->
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
        })
    }

    fun onCancelClick() {
        onFinish(null)
    }
}