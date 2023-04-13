package com.denchic45.kts.ui.yourStudyGroups

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

@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent) {
    val groups by component.studyGroups.collectAsState()

    Column {
        groups.onSuccess {
            it.forEachIndexed { index, group ->
                TimetableItem(group.name) { component.onGroupSelect(index) }
            }
        }
    }
}

@Composable
private fun TimetableItem(title: String, onClick: () -> Unit) {
    Text(text = title, modifier = Modifier
        .clickable { onClick() }
        .padding(8.dp))
}