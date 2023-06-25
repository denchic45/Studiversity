package com.denchic45.studiversity.ui.yourworks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun YourUpcomingWorksScreen(component: YourUpcomingWorksComponent) {
    val works by component.observedWorks.collectAsState()
    WorksListContent(works, component::onWorkClick)
}