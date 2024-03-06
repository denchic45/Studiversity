package com.denchic45.studiversity.ui.yourworks

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun YourUpcomingWorksScreen(component: YourUpcomingWorksComponent) {
    val works by component.works.collectAsState()
    val refreshing by component.refreshing.collectAsState()
    val refreshState = rememberPullRefreshState(refreshing, { component.refreshing.value = true })
    WorksListContent(works, component::onWorkClick, refreshing,refreshState)
}