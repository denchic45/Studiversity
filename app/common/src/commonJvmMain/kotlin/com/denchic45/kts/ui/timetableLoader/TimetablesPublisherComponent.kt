package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.usecase.PutTimetableUseCase
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class TimetablesPublisherComponent(
    metaRepository: MetaRepository,
    private val putTimetableUseCase: PutTimetableUseCase,
    private val _dayTimetableEditorComponent: (
        timetable: TimetableResponse,
        studyGroupId: UUID,
        _selectedDate: StateFlow<LocalDate>,
        ComponentContext,
    ) -> DayTimetableEditorComponent,
    @Assisted
    private val weekOfYear: String,
    @Assisted
    _studyGroupTimetables: List<Pair<StudyGroupResponse, TimetableResponse>>,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val bellSchedule = metaRepository.observeBellSchedule

    val studyGroups = MutableStateFlow(_studyGroupTimetables.map { it.first })

    val selectedDate = MutableStateFlow(LocalDate.now())

    private val editorComponents = MutableStateFlow(
        _studyGroupTimetables.map {
            _dayTimetableEditorComponent(
                it.second,
                it.first.id,
                selectedDate,
                componentContext.childContext("DayTimetable ${it.first.id}")
            )
        }
    )

    val isEdit = MutableStateFlow(false)

    val viewStates: MutableStateFlow<List<StateFlow<DayTimetableViewState>>> =
        MutableStateFlow(emptyList())

    init {
        componentScope.launch {
            editorComponents.value.map { getViewState(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getViewState(component: DayTimetableEditorComponent) =
        bellSchedule.flatMapLatest { schedule ->
            selectedDate.flatMapLatest { selectedDate ->
                isEdit.flatMapLatest { isEdit ->
                    component.editingWeekTimetable[selectedDate.dayOfWeek.ordinal]
                        .map(componentScope) {
                            it.toTimetableViewState(selectedDate, schedule, isEdit)
                        }
                }
            }
        }.stateIn(componentScope)

    val selectedGroup = MutableStateFlow(0)

    fun onAddStudyGroup(studyGroupResponse: StudyGroupResponse) {
        studyGroups.update { it + studyGroupResponse }

//        val list = List(6) {
//            MutableStateFlow(listOf<PeriodResponse>())
//        }

        val dayTimetableEditorComponent = _dayTimetableEditorComponent(
            TimetableResponse(
                weekOfYear,
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                listOf()
            ),
            studyGroupResponse.id,
            selectedDate,
            componentContext.childContext("DayTimetable")
        )
        editorComponents.update { components ->
            components + dayTimetableEditorComponent
        }
        componentScope.launch {
            viewStates.update { it + getViewState(dayTimetableEditorComponent) }
        }

    }

    fun onRemoveStudyGroup(position: Int) {
        studyGroups.update { it - it[position] }
        if (selectedGroup.value == editorComponents.value.size - 1 && selectedGroup.value != 0) {
            selectedGroup.update { it - 1 }
        }
        editorComponents.update { it - it[position] }
        viewStates.update { it - it[position] }
    }

    fun onStudyGroupClick(position: Int) {
        selectedGroup.value = position
    }

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    fun onEditEnableClick(edit: Boolean) {
        isEdit.value = edit
    }

    fun onPeriodEdit(timetablePos: Int, periodPos: Int) {
        editorComponents.value[timetablePos].onPeriodEdit(periodPos)
    }

    fun onPublishClick() {
        componentScope.launch {
            editorComponents.value.map { async { putTimetableUseCase(weekOfYear, it.request) } }
                .awaitAll()
        }
    }

    enum class PublishState { PREPARATION, SENDING, DONE }

}