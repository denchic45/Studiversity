@file:OptIn(ExperimentalMaterial3Api::class)

package com.denchic45.studiversity.ui.schedule

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.data.service.model.PeriodTime
import com.denchic45.studiversity.ui.ResourceContent


@Composable
fun ScheduleDialog(component: ScheduleComponent, onDismissRequest: () -> Unit) {
    val scheduleResource by component.scheduleResource.collectAsState()

    ResourceContent(resource = scheduleResource) { schedule ->
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Расписание звонков") },
            text = {
                ScheduleContent(schedule)
            },
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("ОК")
                }
            }
        )
    }
}

@Preview
@Composable
fun SchedulePreview() {
    ScheduleContent(
        BellSchedule(
            periods = listOf(
                PeriodTime("8:00", "9:30"),
                PeriodTime("9:40", "11:10"),
                PeriodTime("11:40", "13:10")
            ),
            lunch = PeriodTime(start = "11:10", end = "11:40")
        )
    )
}