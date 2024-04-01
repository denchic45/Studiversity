package com.denchic45.studiversity.ui.search

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.specialty.SpecialtyListItem

@Composable
fun SpecialtyChooserScreen(component: SpecialtyChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id }, itemContent = { item ->
            SpecialtyListItem(item)
        })
}