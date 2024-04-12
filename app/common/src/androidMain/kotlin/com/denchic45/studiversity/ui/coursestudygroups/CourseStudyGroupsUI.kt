package com.denchic45.studiversity.ui.coursestudygroups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.search.StudyGroupChooserScreen
import com.denchic45.studiversity.ui.uiTextOf

@Composable
fun CourseStudyGroupsScreen(component: CourseStudyGroupsComponent) {
    val studyGroups by component.studyGroups.collectAsState()
    val childSlot by component.childSlot.subscribeAsState()
    updateAppBarState(AppBarContent(uiTextOf("Группы курса")))

    Surface(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        CourseStudyGroupsContent(studyGroupsResource = studyGroups, onRemove = component::onStudyGroupRemove)
    }

    childSlot.child?.let {
        when (val child = it.instance) {
            is CourseStudyGroupsComponent.Child.StudyGroupChooser -> {
                StudyGroupChooserScreen(child.component)
            }
        }
    }
}