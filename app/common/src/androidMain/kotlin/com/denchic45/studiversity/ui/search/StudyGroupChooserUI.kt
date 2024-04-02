package com.denchic45.studiversity.ui.search

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.ui.studygroup.StudyGroupListItem

@Composable
fun StudyGroupChooserScreen(
    component: StudyGroupChooserComponent,
) {
    SearchScreen(
        component = component,
        keyItem = StudyGroupItem::id,
        itemContent = {
            StudyGroupListItem(it)
        })
}