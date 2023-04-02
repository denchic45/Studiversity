package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.ui.timetableEditor.DayTimetableEditorComponent
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TimetablesPublisherComponent(
    private val _dayTimetableEditorComponent: (weekOfYear: String,List<MutableStateFlow<List<PeriodResponse>>>) -> DayTimetableEditorComponent,
    @Assisted
    private val weekOfYear: String,
    @Assisted
    _studyGroupTimetables: List<Pair<StudyGroupResponse, TimetableResponse>>,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val studyGroups = MutableStateFlow(_studyGroupTimetables.map { it.first })

    val dayTimetableEditorComponents = MutableStateFlow(
        _studyGroupTimetables.map {
            _dayTimetableEditorComponent(weekOfYear,it.second.days.map(::MutableStateFlow))
        }
    )

    val selectedGroup = MutableStateFlow(0)

    fun onAddStudyGroup(studyGroupResponse: StudyGroupResponse) {
        studyGroups.update { it + studyGroupResponse }

        val list = List(6) {
            MutableStateFlow(listOf<PeriodResponse>())
        }

        dayTimetableEditorComponents.update { components ->
            components + _dayTimetableEditorComponent(weekOfYear,list)
        }
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