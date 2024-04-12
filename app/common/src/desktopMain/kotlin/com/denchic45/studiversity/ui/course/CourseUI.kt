package com.denchic45.studiversity.ui.course

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.courseeditor.CourseEditorDialog
import com.denchic45.studiversity.ui.courseelements.CourseElementsScreen
import com.denchic45.studiversity.ui.coursemembers.CourseMembersScreen
import com.denchic45.studiversity.ui.coursestudygroups.CourseStudyGroupsScreen
import com.denchic45.studiversity.ui.coursetimetable.CourseTimetableScreen
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsDialog
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.main.NavigationIconBack
import com.denchic45.studiversity.ui.theme.spacing

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CourseScreen(component: CourseComponent) {
    val courseResource by component.course.collectAsState()
    val childPages by component.childPages.subscribeAsState()
    val childSlot by component.childSlot.subscribeAsState()
    val allowEdit by component.allowEdit.collectAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is CourseComponent.SlotChild.CourseEditor -> CourseEditorDialog(child.component)
            CourseComponent.SlotChild.Info -> courseResource.onSuccess { course ->
                CourseInfoDialog(course, component::onDialogClose)
            }

            is CourseComponent.SlotChild.Topics -> CourseTopicsDialog(child.component, component::onDialogClose)
        }
    }
    ResourceContent(courseResource) { course ->
        Column {
            CustomAppBar(
                title = { AppBarTitle(course.name) },
                actions = {
                    IconButton(onClick = component::onInfoClick) { Icon(Icons.Outlined.Info, null) }
                    if (allowEdit) {
                        IconButton(onClick = component::onTopicsClick) { Icon(Icons.Outlined.ViewDay, null) }
                        IconButton(onClick = component::onEditClick) { Icon(Icons.Outlined.Edit, null) }
                    }
                },
                navigationContent = { NavigationIconBack(onClick = component::onBack) }
            )

            val activeChildPageInstance = childPages.items[childPages.selectedIndex].instance
            Row {
                Column(Modifier.requiredWidth(300.dp).padding(end = MaterialTheme.spacing.normal)) {
                    childPages.items.forEachIndexed { index, child ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = child.configuration.tabTitle(),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            onClick = { component.onTabSelect(index) },
                            selected = index == childPages.selectedIndex
                        )
                    }
                }

                activeChildPageInstance?.let {
                    Box(contentAlignment = Alignment.TopCenter) {
                        AnimatedContent(activeChildPageInstance,
                            transitionSpec = {
                                (slideInVertically { -it / 8 } + fadeIn())
                                    .togetherWith(slideOutVertically { it / 8 } + fadeOut())
                            }
                        ) { child ->
                            Box(Modifier.fillMaxSize()) {
                                when (child) {
                                    is CourseComponent.TabChild.Elements -> {
                                        CourseElementsScreen(child.component, allowEdit)
                                    }

                                    is CourseComponent.TabChild.Members -> {
                                        CourseMembersScreen(child.component, allowEdit)
                                    }

                                    is CourseComponent.TabChild.Timetable -> {
                                        CourseTimetableScreen(child.component)
                                    }

                                    is CourseComponent.TabChild.StudyGroups -> {
                                        CourseStudyGroupsScreen(child.component, allowEdit)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}