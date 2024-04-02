package com.denchic45.studiversity.ui.schedule

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.data.service.model.PeriodTime
import com.denchic45.studiversity.util.toLocalTime

@Composable
fun ScheduleContent(schedule: BellSchedule) {
    LazyColumn(horizontalAlignment = Alignment.Start) {
        val indexedSchedule = schedule.periods.withIndex()
        schedule.lunch?.let { lunch ->
            val beforeLunch = indexedSchedule.takeWhile {
                it.value.start.toLocalTime("H:mm") < lunch.start.toLocalTime("H:mm")
            }
            val afterLunch = indexedSchedule - beforeLunch.toSet()
            items(beforeLunch) { item ->
                PeriodListItem(item.index + 1, item.value)
                Divider()
            }
            item {
                LunchPeriodListItem(item = lunch)
                Divider()
            }
            items(afterLunch) { item ->
                PeriodListItem(item.index + 1, item.value)
                Divider()
            }
        } ?: itemsIndexed(schedule.periods) { index, item ->
            PeriodListItem(index + 1, item)
            Divider()
        }
    }
}

@Composable
private fun LunchPeriodListItem(item: PeriodTime) {
    Row(Modifier) {
        ListItem(
            headlineContent = {
                Text(
                    text = "перерыв",
                    style = MaterialTheme.typography.titleLarge
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