package com.denchic45.kts.ui.periodeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.chooser.CourseChooserScreen
import com.denchic45.kts.ui.chooser.UserChooserScreen
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodEditorScreen(component: PeriodEditorComponent, appBarInteractor: AppBarInteractor) {
    component.lifecycle.doOnStart {
        appBarInteractor.update { it.copy(visible = false) }
    }
    component.lifecycle.doOnDestroy {
        appBarInteractor.update { it.copy(visible = true) }
    }
    val childOverlay by component.childOverlay.subscribeAsState()

    Surface {
        Column {
            when (val overlayChild = childOverlay.overlay?.instance) {
                is PeriodEditorComponent.OverlayChild.CourseChooser -> {
                    CourseChooserScreen(
                        component = overlayChild.component,
                        appBarInteractor = appBarInteractor
                    )
                }

                is PeriodEditorComponent.OverlayChild.UserChooser -> {
                    UserChooserScreen(
                        component = overlayChild.component,
                        appBarInteractor = appBarInteractor
                    )
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
                    PeriodEditorContent(state = component.state)
                }
            }
        }
    }
}

@Composable
fun PeriodEditorContent(state: EditingPeriod) {
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
            ListItem(
                headlineContent = {
                    TransparentTextField(
                        value = state.room?.name ?: "",
                        onValueChange = {},
                        placeholder = "Аудитория"
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DoorFront,
                        contentDescription = "date"
                    )
                }
            )
        }
    }
}

@Composable
fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    placeholder: String? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
                innerTextField()
            }
        }
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
            }
        )
    }
}