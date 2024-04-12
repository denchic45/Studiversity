package com.denchic45.studiversity.ui.studygroup

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
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
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.main.NavigationIconBack
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorDialog
import com.denchic45.studiversity.ui.studygroupmembers.StudyGroupMembersScreen
import com.denchic45.studiversity.ui.theme.spacing


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun StudyGroupScreen(component: StudyGroupComponent) {
    val studyGroupResource by component.studyGroup.collectAsState()
    val childPages by component.childPages.subscribeAsState()
    val childSlot by component.childSlot.subscribeAsState()
    val allowEdit by component.allowEdit.collectAsState()

    childSlot.child?.let {
        when (val child = it.instance) {
            is StudyGroupComponent.SlotChild.StudyGroupEditor -> StudyGroupEditorDialog(child.component)
            StudyGroupComponent.SlotChild.Info -> studyGroupResource.onSuccess { studyGroup ->
                StudyGroupInfoDialog(studyGroup, component::onDialogClose)
            }
        }
    }

    ResourceContent(studyGroupResource) { studyGroup ->
        Column {
            CustomAppBar(
                title = { AppBarTitle(studyGroup.name) },
                actions = {
                    IconButton(onClick = component::onInfoClick) { Icon(Icons.Outlined.Info, null) }
                    if (allowEdit.takeValueIfSuccess() == true)
                        IconButton(onClick = component::onEditClick) { Icon(Icons.Outlined.Edit, null) }
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
                                    is StudyGroupComponent.TabChild.Members -> {
                                        StudyGroupMembersScreen(child.component)
                                    }

                                    is StudyGroupComponent.TabChild.Courses -> {
                                        StudyGroupCoursesScreen(child.component)
                                    }

                                    is StudyGroupComponent.TabChild.Timetable -> {
                                        StudyGroupTimetableScreen(child.component)
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