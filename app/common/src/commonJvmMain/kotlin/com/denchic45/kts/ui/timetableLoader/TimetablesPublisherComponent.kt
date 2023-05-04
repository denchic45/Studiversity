package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.flatMapLatest
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class TimetablesPublisherComponent(
    private val metaRepository: MetaRepository,
    private val _dayTimetableEditorComponent: (
        timetable: TimetableResponse,
        studyGroupId: UUID,
        _selectedDate: Flow<LocalDate>,
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

    val studyGroups = MutableStateFlow(_studyGroupTimetables.map { it.first })

     val selectedDate = MutableStateFlow(LocalDate.now())

    val dayTimetableEditorComponents = MutableStateFlow(
        _studyGroupTimetables.map {
            _dayTimetableEditorComponent(
                it.second,
                it.first.id,
                selectedDate,
                componentContext.childContext("DayTimetable ${it.first.id}")
            )
        }
    )

    private val isEdit = MutableStateFlow(false)

    private val bellSchedule = metaRepository.observeBellSchedule

    val viewStates: MutableStateFlow<List<Flow<DayTimetableViewState>>> =
        MutableStateFlow(dayTimetableEditorComponents.value.map { component ->
            getViewState(component)
        })

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getViewState(component: DayTimetableEditorComponent) = bellSchedule.flatMapLatest { schedule ->
            selectedDate.flatMapLatest(componentScope) { selectedDate ->
                isEdit.flatMapLatest(componentScope) { isEdit ->
                component.editingWeekTimetable[selectedDate.dayOfWeek.ordinal].map(componentScope) {
                        it.toTimetableViewState(selectedDate, schedule, isEdit)
                    }
                }
            }
        }

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
        dayTimetableEditorComponents.update { components ->
            components + dayTimetableEditorComponent
        }
        viewStates.update { it + getViewState(dayTimetableEditorComponent) }

    }

    fun onRemoveStudyGroup(position: Int) {
        studyGroups.update { it - it[position] }
        if (selectedGroup.value == dayTimetableEditorComponents.value.size - 1 && selectedGroup.value != 0) {
            selectedGroup.update { it - 1 }
        }
        dayTimetableEditorComponents.update { it - it[position] }
    }

    fun onStudyGroupClick(position: Int) {
        selectedGroup.value = position
    }

}