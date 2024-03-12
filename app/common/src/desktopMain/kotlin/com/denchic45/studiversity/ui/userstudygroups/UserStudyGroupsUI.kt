package com.denchic45.studiversity.ui.userstudygroups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.courses.CourseListItem
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem

@Composable
fun UserCoursesScreen(component: UserStudyGroupsComponent) {
    val studyGroups by component.studyGroupsByUser.collectAsState()
    AlertDialog(
        onDismissRequest = component::onDismissRequest,
        title = { Text("Группы") },
        confirmButton = { TextButton(onClick = {}) { Text("ОК") } },
        text = {
            ResourceContent(studyGroups) {
                LazyColumn {
                    items(it) {
                        StudyGroupListItem(it, Modifier.clickable { component.onCourseClick(it.id) })
                    }
                }
            }
        }
    )
}