package com.denchic45.studiversity.ui.coursestudygroups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun CourseStudyGroupsScreen(component: CourseStudyGroupsComponent) {
    val studyGroups by component.studyGroups.collectAsState()
}