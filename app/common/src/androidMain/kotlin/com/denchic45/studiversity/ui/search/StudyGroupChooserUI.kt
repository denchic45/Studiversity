package com.denchic45.studiversity.ui.search

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem

@Composable
fun StudyGroupChooserScreen(
    component: StudyGroupChooserComponent,
) {
    SearchScreen(
        component = component,
        keyItem = { it.id },
        itemContent = {
            StudyGroupListItem(it)
        })
}