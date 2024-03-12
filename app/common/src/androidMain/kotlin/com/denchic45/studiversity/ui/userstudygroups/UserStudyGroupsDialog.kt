package com.denchic45.studiversity.ui.userstudygroups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem

@Composable
fun UserStudyGroupsDialog(component: UserStudyGroupsComponent) {
    val courses by component.studyGroupsByUser.collectAsState()

    ResourceContent(courses) {
        LazyColumn {
            items(it) {
                StudyGroupListItem(it, Modifier.clickable { component.onCourseClick(it.id) })
            }
        }
    }
}