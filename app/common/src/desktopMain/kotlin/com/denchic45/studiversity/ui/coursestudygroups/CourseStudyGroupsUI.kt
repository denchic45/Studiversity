package com.denchic45.studiversity.ui.coursestudygroups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar

@Composable
fun CourseStudyGroupsScreen(component: CourseStudyGroupsComponent, allowEdit: Boolean) {
    val studyGroups by component.studyGroups.collectAsState()

    BlockContent {
        ScreenScaffold(topBar = {
            CustomAppBar(
                title = { AppBarTitle("Группы") },
                actions = {
                    if (allowEdit)
                        Button(onClick = component::onAddStudyGroupClick) {
                            Icon(Icons.Outlined.Add, null)
                            Text("Добавить группу")
                        }
                })
        }) {
            CourseStudyGroupsContent(studyGroups, component::onStudyGroupRemove)
        }
    }
}