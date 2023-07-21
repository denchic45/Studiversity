package com.denchic45.studiversity.ui.coursestudygroups

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.search.StudyGroupChooserScreen
import com.denchic45.studiversity.ui.search.StudyGroupListItem
import com.denchic45.studiversity.ui.uiTextOf

@Composable
fun CourseStudyGroupsScreen(component: CourseStudyGroupsComponent) {
    val studyGroups by component.studyGroups.collectAsState()
    val childOverlay by component.childOverlay.subscribeAsState()
    updateAppBarState(AppBarContent(uiTextOf("Группы курса")))

    Surface(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)) {
        ResourceContent(resource = studyGroups) { studyGroups ->
            LazyColumn {
                item {
                    ListItem(
                        headlineContent = { Text(text = "Добавить группу") },
                        leadingContent = { Icon(Icons.Default.Add, "add study group") },
                        modifier = Modifier.clickable { component.onAddStudyGroupClick() }
                    )
                }
                items(studyGroups) {
                    StudyGroupListItem(item = it, trailingContent = {
                        IconButton(onClick = { component.onStudyGroupRemove(it) }) {
                            Icon(
                                Icons.Outlined.Delete,
                                "delete study group"
                            )
                        }
                    })
                }
            }
        }
    }

    childOverlay.overlay?.let {
        when(val child =it.instance) {
            is CourseStudyGroupsComponent.Child.StudyGroupChooser -> StudyGroupChooserScreen(
                child.component
            )
        }
    }

}