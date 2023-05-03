package com.denchic45.kts.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.ui.timetable.state.toLocalDateOfWeekOfYear
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.copy
import com.denchic45.stuiversity.api.timetable.model.EventRequest
import com.denchic45.stuiversity.api.timetable.model.EventResponse
import com.denchic45.stuiversity.api.timetable.model.LessonRequest
import com.denchic45.stuiversity.api.timetable.model.LessonResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.PeriodRequest
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Inject
class DayTimetableEditorComponent constructor(
    metaRepository: MetaRepository,
    @Assisted
    private val _selectedDate: LocalDate,
    @Assisted
    private val _weekTimetable: List<List<PeriodResponse>>,
    @Assisted
    private val onFinish: (List<List<PeriodRequest>>?) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val selectedDate = MutableStateFlow(_selectedDate)

    private val mondayDate = _selectedDate.format(DateTimeFormatter.ofPattern("YYYY_ww"))
        .toLocalDateOfWeekOfYear()

    private val selectedDay = selectedDate.map { it.dayOfWeek.ordinal }.stateIn(
        componentScope,
        SharingStarted.Lazily,
        selectedDate.value.dayOfWeek.ordinal
    )

    private val editingWeekTimetable: List<MutableStateFlow<List<PeriodResponse>>> = listOf(
        MutableStateFlow(_weekTimetable[0]),
        MutableStateFlow(_weekTimetable[1]),
        MutableStateFlow(_weekTimetable[2]),
        MutableStateFlow(_weekTimetable[3]),
        MutableStateFlow(_weekTimetable[4]),
        MutableStateFlow(_weekTimetable[5]),
    )


    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(FlowPreview::class)
    val viewState = combine(selectedDay, bellSchedule) { selected, schedule ->
        editingWeekTimetable[selected].map {
            it.toTimetableViewState(
                date = mondayDate.plusDays(selected.toLong()),
                bellSchedule = schedule,
                isEdit = true
            )
        }
    }.flattenConcat().shareIn(componentScope, SharingStarted.Lazily, 1)


    fun onAddPeriod(period: PeriodResponse) {
        editingWeekTimetable[selectedDay.value].update { it + period }
    }

    fun onUpdatePeriod(position: Int, period: PeriodResponse) {
        editingWeekTimetable[selectedDay.value].update {
            it.copy {
                this[position] = period
            }
        }
    }

    fun onRemovePeriod(position: Int) {
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
        })
    }

    fun onCancelClick() {
        onFinish(null)
    }
}