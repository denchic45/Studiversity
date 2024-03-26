package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.ActionMenuItem2
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.uiIconOf
import com.denchic45.studiversity.ui.uiTextOf

@Composable
fun StudyGroupEditorScreen(component: StudyGroupEditorComponent) {
    val stateResource by component.viewState.collectAsState()
    val allowSave by component.allowSave.collectAsState()
    val isNew = component.isNew

    updateAnimatedAppBarState(
        allowSave,
        AppBarContent(
            title = uiTextOf(
                if (isNew) "Новая группа" else "Редактировать группу"
            ),
            actionItems = listOf(
                ActionMenuItem2(
                    icon = uiIconOf(Icons.Default.Done),
                    enabled = allowSave,
                    onClick = component::onSaveClick
                )
            )
        )
    )

    Surface(Modifier.fillMaxSize()) {
        ResourceContent(resource = stateResource) {
            StudyGroupEditorContent(
                state = it,
                onNameChange = component::onNameChange,
                onStartAcademicYearChange = component::onStartYearChange,
                onEndAcademicYearChange = component::onEndYearChange,
                onSpecialtyQueryChange = component::onSpecialtyQueryChange,
                onSpecialtySelect = component::onSpecialtySelect,
                onRemoveStudyGroupClick = component::onRemoveStudyGroupClick
            )
        }
    }
}