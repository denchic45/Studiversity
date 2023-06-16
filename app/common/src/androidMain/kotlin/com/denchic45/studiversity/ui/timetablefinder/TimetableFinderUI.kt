package com.denchic45.studiversity.ui.timetablefinder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.appbar2.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.LocalAppBarState
import com.denchic45.studiversity.ui.appbar2.updateAppBarState
import com.denchic45.studiversity.ui.periodeditor.PeriodEditorScreen
import com.denchic45.studiversity.ui.search.IconTitleBox
import com.denchic45.studiversity.ui.search.StudyGroupListItem
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.timetable.DayTimetableContent
import com.denchic45.studiversity.ui.timetable.getMonthTitle
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.time.LocalDate

@Composable
fun TimetableFinderScreen(
    component: TimetableFinderComponent,
) {
    val selectedDate by component.selectedDate.collectAsState()
    val selectedYearWeek by component.selectedWeekOfYear.collectAsState()
    val state = remember(component::state)
    val timetableResource by component.timetable.collectAsState()

    val overlay by component.childOverlay.subscribeAsState()

    when (val child = overlay.overlay?.instance) {
        is TimetableFinderComponent.OverlayChild.PeriodEditor -> {
            PeriodEditorScreen(child.component)
        }

        null -> {
            updateAppBarState(
                key1 = selectedYearWeek,
                key2 = timetableResource,
                key3 = state.selectedStudyGroup,
                content = AppBarContent(
                    title = uiTextOf(
                        if (state.selectedStudyGroup != null) getMonthTitle(selectedYearWeek)
                        else ""
                    ), actionItems = when (val resource = timetableResource) {
                        is Resource.Success -> {
                            if (resource.value.isEdit) {
                                listOf(
                                    ActionMenuItem2(
                                        icon = uiIconOf(Icons.Default.Done),
                                        onClick = component::onSaveChangesClick
                                    )
                                )
                            } else {
                                listOf(
                                    ActionMenuItem2(
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
    Surface {
        var active by remember { mutableStateOf(false) }
        Box(modifier = Modifier
            .semantics { isContainer = true }
            .zIndex(1f)
            .fillMaxWidth()) {
            SearchBar(
                query = if (state.selectedStudyGroup != null && !active) state.selectedStudyGroup!!.name
                else state.query,
                onQueryChange = onQueryType,
                onSearch = {},
                active = active,
                placeholder = { Text("Поиск группы") },
                onActiveChange = { active = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search"
                    )
                },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                state.foundGroups.onSuccess { groups ->
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(items = groups, key = { it.id }) {
                            StudyGroupListItem(
                                item = it,
                                modifier = Modifier.clickable {
                                    active = false
                                    onGroupSelect(it)
                                }
                            )
                        }
                    }
                }
            }
        }

        val scrollableWeeks = when (timetableResource) {
            Resource.Loading, is Resource.Error -> true
            is Resource.Success -> !timetableResource.value.isEdit
        }
        Box(
            modifier = Modifier.padding(
                PaddingValues(
                    start = MaterialTheme.spacing.normal,
                    top = 72.dp,
                    end = MaterialTheme.spacing.normal,
                    bottom = MaterialTheme.spacing.normal
                )
            )
        ) {
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
            } ?: IconTitleBox(icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_study_group),
                    contentDescription = "search study group",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(78.dp)
                )
            }, title = {
                Text(
                    text = "Выберите группу"
                )
            })
        }
    }
}