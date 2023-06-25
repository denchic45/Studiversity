package com.denchic45.studiversity.ui.yourworks

import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun YourOverdueWorksScreen(component: YourOverdueWorksComponent) {
    val works by component.works.collectAsState()
    val refreshing by component.refreshing.collectAsState()
    val refreshState = rememberPullRefreshState(refreshing, { component.refreshing.value = true })
    WorksListContent(works, component::onWorkClick, refreshing, refreshState)
}