package com.denchic45.kts.ui.timetablefinder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.ui.chooser.StudyGroupItemUI
import com.denchic45.kts.ui.timetable.DayTimetableContent
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.TimetableState
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.time.LocalDate

@Composable
fun TimetableFinderScreen() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableFinderContent(
    selectedDate:LocalDate,
    state: TimetableFinderState,
    timetableResource:Resource<TimetableState>,
    onQueryType: (String) -> Unit,
    onDateSelect:(LocalDate)->Unit
) {
    Column(Modifier.fillMaxSize()) {
        var active by remember { mutableStateOf(false) }
        SearchBar(
            query = state.query,
            onQueryChange = onQueryType,
            onSearch = {},
            active = active,
            onActiveChange = { active = it }) {
            LazyColumn {
                items(items = state.foundGroups, key = { it.id }) {
                    StudyGroupItemUI(response = it)
                }
            }
        }
        DayTimetableContent(selectedDate = selectedDate,
            timetableResource = timetableResource,
            onDateSelect = onDateSelect)
    }
}

class TimetableFinderState {
    var query by mutableStateOf("")
    var foundGroups by mutableStateOf(emptyList<StudyGroupResponse>())
    var selectedStudyGroup by mutableStateOf<StudyGroupResponse?>(null)
    var timetable by mutableStateOf<DayTimetableViewState?>(null)
}