package com.denchic45.studiversity.ui.yourworks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun YourSubmittedWorksScreen(component: YourSubmittedWorksComponent) {
    val works by component.works.collectAsState()
    WorksListContent(works, component::onWorkClick)
}