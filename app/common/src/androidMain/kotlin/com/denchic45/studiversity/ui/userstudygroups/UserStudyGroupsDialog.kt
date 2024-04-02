package com.denchic45.studiversity.ui.userstudygroups

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.studygroup.StudyGroupListItem

@Composable
fun UserStudyGroupsDialog(component: UserStudyGroupsComponent) {
    val courses by component.studyGroupsByUser.collectAsState()

    ResourceContent(courses) {
        LazyColumn {
            items(it) {
                StudyGroupListItem(it, onClick = { component.onCourseClick(it.id) })
            }
        }
    }
}