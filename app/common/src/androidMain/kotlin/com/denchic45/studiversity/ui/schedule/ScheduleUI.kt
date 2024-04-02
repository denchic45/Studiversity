package com.denchic45.studiversity.ui.schedule

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.uiTextOf


@Composable
fun ScheduleScreen(component: ScheduleComponent) {
    val scheduleResource by component.scheduleResource.collectAsState()

    updateAppBarState(AppBarContent(uiTextOf("Расписание звонков")))

    ResourceContent(resource = scheduleResource) { schedule ->
        ScheduleContent(schedule)
    }
}
