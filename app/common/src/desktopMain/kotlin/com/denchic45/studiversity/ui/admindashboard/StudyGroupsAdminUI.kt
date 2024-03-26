package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorDialog
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem

@Composable
fun StudyGroupsAdminScreen(component: StudyGroupsAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupsAdminComponent.Child.StudyGroupEditor -> StudyGroupEditorDialog(child.component)
        }
    }

    AdminSearchScreen(
        component,
        StudyGroupItem::id,
        searchPlaceholder = "Найти группу",
        fabText = "Создать группу"
    ) {
        StudyGroupListItem(it)
    }
}