package com.denchic45.kts.ui.ownTimetables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.timetableEditor.DayTimetableScreen

@Composable
fun OwnTimetablesScreen(component: OwnTimetablesComponent) {
    val groups by component.studyGroups.collectAsState()

    Column {
        TimetableItem("Мое расписание") { component.onTimetableSelect(-1) }
        groups.onSuccess {
            it.forEachIndexed { index, group ->
                TimetableItem(group.name) { component.onTimetableSelect(index) }
            }
        }
    }

    val timetableComponent by component.timetableComponent.collectAsState()
    timetableComponent.onSuccess {
        DayTimetableScreen(it)
    }
}

@Composable
private fun TimetableItem(title: String, onClick: () -> Unit) {
    Text(text = title, modifier = Modifier.clickable { onClick() })
}