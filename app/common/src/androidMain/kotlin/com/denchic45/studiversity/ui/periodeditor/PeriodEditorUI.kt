package com.denchic45.studiversity.ui.periodeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.ifSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar2.hideAppBar
import com.denchic45.studiversity.ui.search.CourseChooserScreen
import com.denchic45.studiversity.ui.search.UserChooserScreen
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodEditorScreen(component: PeriodEditorComponent) {
    hideAppBar()

    val childOverlay by component.childOverlay.subscribeAsState()
    val foundRooms by component.foundRooms.collectAsState()

    Surface {
        Column {
            when (val overlayChild = childOverlay.overlay?.instance) {
                is PeriodEditorComponent.OverlayChild.CourseChooser -> {
                    CourseChooserScreen(component = overlayChild.component)
                }

                is PeriodEditorComponent.OverlayChild.UserChooser -> {
                    UserChooserScreen(component = overlayChild.component)
                }

                null -> {
                    var expanded by remember { mutableStateOf(false) }
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .clickable { expanded = true },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (component.state.details) {
                                        is EditingPeriodDetails.Event -> "Событие"
                                        is EditingPeriodDetails.Lesson -> "Урок"
                                    }
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "dropdown period type"
                                )
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        },
                        actions = {
                            IconButton(onClick = { component.onSaveClick() }) {
                                Icon(imageVector = Icons.Default.Done, contentDescription = "save")
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { component.onCloseClick() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "close"
                                )
                            }
                        }
                    )
                    Box {
                        DropdownMenu(expanded = expanded,
                            offset = DpOffset(48.dp, 0.dp),
                            onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Урок") },
                                onClick = {
                                    expanded = false
                                    component.onDetailsTypeSelect(PeriodEditorComponent.DetailsType.LESSON)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Событие") },
                                onClick = {
                                    expanded = false
                                    component.onDetailsTypeSelect(PeriodEditorComponent.DetailsType.EVENT)
                                }
                            )
                        }
                    }
                    Children(component.childDetailsStack) {
                        when (val child = it.instance) {
                            is PeriodEditorComponent.DetailsChild.Event -> {
                                EventDetailsEditorScreen(
                                    component = child.component
                                )
                            }

                            is PeriodEditorComponent.DetailsChild.Lesson -> {
                                LessonDetailsEditorScreen(
                                    component = child.component,
                                    members = component.state.members,
                                    onAddMemberClick = component::onAddMemberClick,
                                    onRemoveMemberClick = component::onRemoveMemberClick
                                )
                            }
                        }
                    }
                    PeriodEditorContent(
                        state = component.state,
                        foundRooms = foundRooms,
                        onRoomType = component::onRoomType,
                        onRoomSelect = component::onRoomSelect,
                        onRoomRemove = component::onRoomRemove
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodEditorContent(
    state: EditingPeriod,
    foundRooms: Resource<List<RoomResponse>>,
    onRoomType: (String) -> Unit,
    onRoomSelect: (RoomResponse) -> Unit,
    onRoomRemove: () -> Unit
) {
    Surface {
        Column(Modifier.fillMaxSize()) {
            Divider(Modifier.padding(vertical = MaterialTheme.spacing.small))
            ListItem(
                headlineContent = { Text(text = state.date.toString(DateTimePatterns.d_MMMM)) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "date"
                    )
                }
            )
            ListItem(
                headlineContent = { Text(text = "Порядковый номер") },
                leadingContent = {
                    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.order.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            )
            var roomText by remember { mutableStateOf(state.room?.name ?: "") }
            var roomsExpanded by remember { mutableStateOf(false) }
            val expanded = roomsExpanded && foundRooms.ifSuccess { it.isNotEmpty() } ?: false
            ListItem(
                headlineContent = {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { roomsExpanded = it },
                    ) {
                        TransparentTextField(
                            value = roomText,
                            onValueChange = {
                                roomText = it
                                roomsExpanded = true
                                onRoomType(it)
                            },
                            placeholder = "Аудитория",
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                        )
//                        OutlinedTextField(
//                            value = state.topicQueryText.takeIf(String::isNotEmpty)
//                                ?: state.selectedTopic?.title?.asString() ?: "",
//                            onValueChange = { onTopicNameType(it) },
//
//                            label = { Text("Раздел") },
//                            trailingIcon = {
//                                ExposedDropdownMenuDefaults.TrailingIcon(
//                                    expanded = showList
//                                )
//                            },
//                            singleLine = true,
//                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
//                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { roomsExpanded = false }) {
                            ResourceContent(resource = foundRooms, onLoading = {}) { rooms ->
                                rooms.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it.name) },
                                        onClick = {
                                            roomText = it.name
                                            roomsExpanded = false
                                            onRoomSelect(it)
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DoorFront,
                        contentDescription = "date"
                    )
                },
                trailingContent = {
                    Box(modifier = Modifier.size(40.dp)) {
                        state.room?.let {
                            IconButton(onClick = {
                                onRoomRemove()
                                roomText = ""
                            }) {
                                Icon(Icons.Default.Close, "remove room")
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun TransparentTextField(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    placeholder: String? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun PeriodEditorPreview() {
    AppTheme {
        PeriodEditorContent(
            state = EditingPeriod(LocalDate.now(), UUID.randomUUID(), "ПКС").apply {
                order = 1
                (details as EditingPeriodDetails.Lesson).course = CourseResponse(
                    id = UUID.randomUUID(),
                    name = "Математика ПКС",
                    subject = SubjectResponse(
                        id = UUID.randomUUID(),
                        name = "Математика",
                        shortname = "Матем",
                        iconUrl = ""
                    ),
                    archived = false
                )
            },
            foundRooms = resourceOf(),
            onRoomType = {},
            onRoomSelect = {},
            onRoomRemove = {}
        )
    }
}