package com.denchic45.kts.ui.timetablefinder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.chooser.StudyGroupItemUI
import com.denchic45.kts.ui.periodeditor.PeriodEditorScreen
import com.denchic45.kts.ui.timetable.DayTimetableContent
import com.denchic45.kts.ui.timetable.state.TimetableState
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.time.LocalDate

@Composable
fun TimetableFinderScreen(
    component: DayTimetableFinderComponent,
    appBarInteractor: AppBarInteractor
) {
    val selectedDate by component.selectedDate.collectAsState()
    val state = component.state
    val timetableResource by component.timetable.collectAsState()

    LaunchedEffect(timetableResource) {
        timetableResource.onSuccess {
            if (it.isEdit) {
                appBarInteractor.set(
                    AppBarState(
                        actions = listOf(
                            ActionMenuItem(id = "save", icon = uiIconOf(
                                Icons.Default.Done
                            ), onClick = { component.onSaveChangesClick() })
                        )
                    )
                )
            } else {
                appBarInteractor.set(AppBarState())
            }
        }
    }

    val overlay by component.childOverlay.subscribeAsState()
    when (val child = overlay.overlay?.instance) {
        is DayTimetableFinderComponent.OverlayChild.PeriodEditor -> PeriodEditorScreen(
            component = child.component,
            appBarInteractor = appBarInteractor
        )

        null -> TimetableFinderContent(
            selectedDate = selectedDate,
            state = state,
            timetableResource = timetableResource,
            onQueryType = component::onQueryType,
            onDateSelect = component::onDateSelect,
            onGroupSelect = component::onGroupSelect,
            onAddPeriodClick = component::onAddPeriodClick,
            onEditPeriodClick = component::onEditPeriodClick,
            onRemovePeriodSwipe = component::onRemovePeriodSwipe
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableFinderContent(
    selectedDate: LocalDate,
    state: TimetableFinderState,
    timetableResource: Resource<TimetableState>,
    onQueryType: (String) -> Unit,
    onDateSelect: (LocalDate) -> Unit,
    onGroupSelect: (StudyGroupResponse) -> Unit,
    onAddPeriodClick: () -> Unit,
    onEditPeriodClick: (Int) -> Unit,
    onRemovePeriodSwipe: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        var active by remember { mutableStateOf(false) }
        SearchBar(
            query = state.query,
            onQueryChange = onQueryType,
            onSearch = {},
            active = active,
            onActiveChange = { active = it }) {
            state.foundGroups.onSuccess { groups ->
                LazyColumn {
                    items(items = groups, key = { it.id }) {
                        StudyGroupItemUI(
                            response = it,
                            modifier = Modifier.clickable { onGroupSelect(it) }
                        )
                    }
                }
            }
        }
        val scrollableWeeks = when (timetableResource) {
            Resource.Loading, is Resource.Error -> true
            is Resource.Success -> !timetableResource.value.isEdit
        }
        DayTimetableContent(
            selectedDate = selectedDate,
            timetableResource = timetableResource,
            onDateSelect = onDateSelect,
            onAddPeriodClick = onAddPeriodClick,
            onEditPeriodClick = onEditPeriodClick,
            onRemovePeriodSwipe = onRemovePeriodSwipe,
            scrollableWeeks = scrollableWeeks
        )
    }
}