package com.denchic45.studiversity.ui.schedule

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import com.denchic45.studiversity.data.service.model.PeriodTime
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.util.toLocalTime


@Composable
fun ScheduleScreen(component: ScheduleComponent) {
    val schedule by component.schedule.collectAsState()

    updateAppBarState(AppBarContent(uiTextOf("Расписание звонков")))

    ResourceContent(resource = schedule) { bellSchedule ->
        val indexed = bellSchedule.periods.withIndex()
        val beforeLunch = indexed.takeWhile {
            it.value.start.toLocalTime("H:mm") < bellSchedule.lunch!!.start.toLocalTime("H:mm") // todo обрабатывать случаи, когда обеда нет
        }

        val afterLunch = indexed - beforeLunch.toSet()

        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(horizontalAlignment = Alignment.Start) {
                    items(beforeLunch) { item ->
                        PeriodListItem(item.index + 1, item.value)
                        Divider()
                    }
                    bellSchedule.lunch?.let { lunch ->
                        item {
                            LunchPeriodListItem(item = lunch)
                            Divider()
                        }
                    }

                    items(afterLunch) { item ->
                        PeriodListItem(item.index + 1, item.value)
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun LunchPeriodListItem(
    item: PeriodTime
) {
    Row(Modifier) {
        ListItem(
            headlineContent = {
                Text(
                    text = "перерыв",
                    style = MaterialTheme.typography.titleLarge,
                    fontStyle = FontStyle.Italic
                )
            },
            trailingContent = {
                Text(
                    text = "${item.start}–${item.end}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
    }
}

@Composable
private fun PeriodListItem(index: Int, time: PeriodTime) {
    ListItem(
        headlineContent = {
            Text(
                text = "пара",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        leadingContent = {
            Text(
                text = "$index",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Text(
                text = "${time.start}–${time.end}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
}
