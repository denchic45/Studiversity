package com.denchic45.kts.ui.yourTimetables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.timetableEditor.DayTimetableScreen

@Composable
fun YourTimetablesScreen(component: YourTimetablesComponent, appBarInteractor: AppBarInteractor) {
    val groups by component.studyGroups.collectAsState()

    Column {
        TimetableItem("Мое расписание") { component.onTimetableSelect(-1) }
        groups.onSuccess {
            it.forEachIndexed { index, group ->
                TimetableItem(group.name) { component.onTimetableSelect(index) }
            }
        }
        val timetableComponent = component.timetableComponent
        DayTimetableScreen(timetableComponent, appBarInteractor)
    }
}

@Composable
private fun TimetableItem(title: String, onClick: () -> Unit) {
    Text(text = title, modifier = Modifier
        .clickable { onClick() }
        .padding(8.dp))
}