package com.denchic45.kts.ui.timetablefinder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.chooser.StudyGroupListItem
import com.denchic45.kts.ui.periodeditor.PeriodEditorScreen
import com.denchic45.kts.ui.timetable.DayTimetableContent
import com.denchic45.kts.ui.timetable.getMonthTitle
import com.denchic45.kts.ui.timetable.state.TimetableState
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.time.LocalDate

@Composable
fun TimetableFinderScreen(
    component: TimetableFinderComponent,
    appBarInteractor: AppBarInteractor,
) {
    val selectedDate by component.selectedDate.collectAsState()
    val selectedYearWeek by component.selectedWeekOfYear.collectAsState()
    val state = remember(component::state)
    val timetableResource by component.timetable.collectAsState()

//    component.timetable.collectWithLifecycle {
//        appBarInteractor.update {
//            it.copy(
//                actions = if ((timetableResource as? Resource.Success)?.value?.isEdit == true) {
//                    listOf(
//                        ActionMenuItem(
//                            id = "save",
//                            icon = uiIconOf(Icons.Default.Done),
//                            onClick = component::onSaveChangesClick
//                        )
//                    )
//                } else {
//                    listOf(
//                        ActionMenuItem(
//                            id = "edit",
//                            icon = uiIconOf(Icons.Outlined.Edit),
//                            onClick = component::onEditClick
//                        )
//                    )
//                }
//            )
//        }
//    }

//    LocalLifecycleOwner.current.lifecycle.currentState

    val overlay by component.childOverlay.subscribeAsState()


//    LaunchedEffect(key1 = Unit ) {
//        snapshotFlow(state::selectedStudyGroup).collectWithLifecycle() {
//            appBarInteractor.set(
//                if (it == null) {
//                    EmptyAppBar
//                } else {
//                    AppBarState(
//                        title = uiTextOf( getMonthTitle (selectedYearWeek)),
//                        actions = if ((timetableResource as? Resource.Success)?.value?.isEdit == true) {
//                            listOf(
//                                ActionMenuItem(
//                                    id = "save",
//                                    icon = uiIconOf(Icons.Default.Done),
//                                    onClick = component::onSaveChangesClick
//                                )
//                            )
//                        } else {
//                            listOf(
//                                ActionMenuItem(
//                                    id = "edit",
//                                    icon = uiIconOf(Icons.Outlined.Edit),
//                                    onClick = component::onEditClick
//                                )
//                            )
//                        }
//                    )
//                }
//            )
//        }
//    }

//    component.lifecycle.doOnStart {
//        appBarInteractor.update { it.copy(visible = true) }
//    }

    when (val child = overlay.overlay?.instance) {
        is TimetableFinderComponent.OverlayChild.PeriodEditor -> PeriodEditorScreen(
            component = child.component,
            appBarInteractor = appBarInteractor
        )

        null -> {
            LaunchedEffect(
                selectedYearWeek,
                timetableResource,
                state.selectedStudyGroup
            ) {
                appBarInteractor.set(
                    AppBarState(
                        title = uiTextOf(
                            if (state.selectedStudyGroup != null) getMonthTitle(selectedYearWeek)
                            else ""
                        ),
                        actions = when (val resource = timetableResource) {
                            is Resource.Success -> {
                                if (resource.value.isEdit) {
                                    listOf(
                                        ActionMenuItem(
                                            id = "save",
                                            icon = uiIconOf(Icons.Default.Done),
                                            onClick = component::onSaveChangesClick
                                        )
                                    )
                                } else {
                                    listOf(
                                        ActionMenuItem(
                                            id = "edit",
                                            icon = uiIconOf(Icons.Outlined.Edit),
                                            onClick = component::onEditClick
                                        )
                                    )
                                }
                            }

                            else -> emptyList()
                        }
                    )
                )
            }
            TimetableFinderContent(
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
    onRemovePeriodSwipe: (Int) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        var active by remember { mutableStateOf(false) }
        SearchBar(
            query = if (state.selectedStudyGroup != null && active) state.selectedStudyGroup!!.name
            else state.query,
            onQueryChange = onQueryType,
            onSearch = {},
            active = active,
            onActiveChange = { active = it },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search"
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            state.foundGroups.onSuccess { groups ->
                LazyColumn {
                    items(items = groups, key = { it.id }) {
                        StudyGroupListItem(
                            response = it,
                            modifier = Modifier.clickable {
                                active = false
                                onGroupSelect(it)
                            }
                        )
                    }
                }
            }
        }
        val scrollableWeeks = when (timetableResource) {
            Resource.Loading, is Resource.Error -> true
            is Resource.Success -> !timetableResource.value.isEdit
        }
        state.selectedStudyGroup?.let {
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
}