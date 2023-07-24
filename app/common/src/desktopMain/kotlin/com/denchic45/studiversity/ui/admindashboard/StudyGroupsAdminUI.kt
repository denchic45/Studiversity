package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse

@Composable
fun StudyGroupsAdminScreen(component: StudyGroupsAdminComponent) {
    AdminSearchScreen(component, StudyGroupResponse::id) {
        Text(it.name)
    }
}