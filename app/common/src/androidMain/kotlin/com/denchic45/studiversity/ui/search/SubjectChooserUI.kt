package com.denchic45.studiversity.ui.search

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.subject.SubjectListItem

@Composable
fun SubjectChooserScreen(component: SubjectChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id },
        itemContent = {
            SubjectListItem(it)
        })
}