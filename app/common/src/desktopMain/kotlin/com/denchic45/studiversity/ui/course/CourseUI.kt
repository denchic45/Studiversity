package com.denchic45.studiversity.ui.course

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
        CustomAppBar(
            title = { AppBarTitle(course.name) },
            actions = {
                IconButton(onClick = component::onInfoClick) { Icon(Icons.Outlined.Info, null) }
                if (allowEdit) {
                    IconButton(onClick = component::onTopicsClick) { Icon(Icons.Outlined.Topic, null) }
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
                                    CourseElementsScreen(child.component)
                                }

                                is CourseComponent.TabChild.Members -> {
                                    CourseMembersScreen(child.component)
                                }

                                is CourseComponent.TabChild.Timetable -> {
                                    CourseTimetableScreen(child.component)
                                }

                                is CourseComponent.TabChild.StudyGroups -> {
                                    CourseStudyGroupsScreen(child.component)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


//@Composable
//fun CourseContent(
//    course: Resource<CourseResponse>,
//    allowEdit: Boolean,
//    children: List<CourseComponent.TabChild>,
//    sidebarChild: ChildSlot<CourseComponent.SidebarConfig, CourseComponent.SidebarChild>,
//    onEditCourseClick: () -> Unit,
//    onOpenTopicsClick: () -> Unit
//) {
//    Card(
//        shape = RoundedCornerShape(16.dp),
//        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
//        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
//        elevation = CardDefaults.cardElevation(0.dp)
//    ) {
//        var selectedTab by remember { mutableStateOf(0) }
//
//        Column {
//            TabRow(selectedTabIndex = selectedTab,
//                modifier = Modifier.width(396.dp),
//                contentColor = MaterialTheme.colorScheme.primary,
//                indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
//                divider = {}) {
//                children.forEachIndexed { index, item ->
//                    Tab(
//                        selectedTab == index,
//                        onClick = { selectedTab = index },
//                        text = { Text(item.title) }
//                    )
//                }
//            }
//            Divider()
//            Row {
//                Box(modifier = Modifier.weight(3f)) {
//                    when (val child = children[selectedTab]) {
//                        is CourseComponent.TabChild.Elements -> TODO()
//                        is CourseComponent.TabChild.Members -> TODO()
//                        is CourseComponent.TabChild.Timetable -> TODO()
//                    }
//                }
//                sidebarChild.child?.instance?.let { sidebar ->
//                    Box(Modifier.weight(1f)) {
//                        when (sidebar) {
////                            is CourseComponent.SidebarChild.ScopeMemberEditor -> TODO()
//                            else -> {}
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
private fun CourseBar(
    allowEdit: Boolean,
    onEditCourseClick: () -> Unit,
    onOpenCourseTopicClick: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().height(64.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!allowEdit) return
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onOpenCourseTopicClick) {
            Icon(
                imageVector = Icons.Outlined.ViewDay,
                contentDescription = "open course topics"
            )
        }
        IconButton(onClick = onEditCourseClick) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "open course editor"
            )
        }
    }
}