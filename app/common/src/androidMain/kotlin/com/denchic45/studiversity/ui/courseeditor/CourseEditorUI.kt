package com.denchic45.studiversity.ui.courseeditor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.search.SubjectChooserScreen
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf
import java.util.UUID


@Composable
fun CourseEditorScreen(component: CourseEditorComponent) {
    val resource by component.stateResource.collectAsState()
    val navigation by component.childSlot.subscribeAsState()
    val allowSave by component.allowSave.collectAsState()

    when (val child = navigation.child?.instance) {
        is CourseEditorComponent.DialogChild.SubjectChooser -> {
            SubjectChooserScreen(child.component)
        }

        null -> {
            updateAnimatedAppBarState(
                allowSave,
                AppBarContent(
                    title = uiTextOf(if (component.isNew) "Новый курс" else "Редактировать курс"),
                    actionItems = listOf(
                        ActionMenuItem2(
                            icon = uiIconOf(Icons.Default.Done),
                            enabled = allowSave,
                            onClick = component::onSaveClick
                        )
                    )
                )
            )

            ResourceContent(resource = resource) { state ->
                CourseEditorContent(
                    state = state,
                    onNameChange = component::onNameChange,
                    onSubjectQueryChange = component::onSubjectQueryChange,
                    onSubjectSelect = component::onSubjectSelect,
                    onSubjectRemove = component::onSubjectClose
                )
            }
        }
    }
}

@Preview
@Composable
fun CourseEditorPreview() {
    CourseEditorContent(
        state = EditingCourse(true).apply {
            name = "Course name"
            subject = CourseEditorComponent.SelectedSubject(
                subjectId = UUID.randomUUID(),
                subjectName = "Subject name"
            )
        },
        onNameChange = {},
        onSubjectQueryChange = {},
        onSubjectSelect = {},
        onSubjectRemove = {})
}