package com.denchic45.studiversity.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.denchic45.studiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun CourseScreen(component: CourseComponent) {
    val course by component.course.collectAsState()
    val allowEdit by component.allowEdit.collectAsState(false)
    val children = component.children
    val childStack by component.childStack.subscribeAsState()
    val sidebarChild by component.childSidebar.subscribeAsState()

    if (childStack.active.instance is CourseComponent.Child.None) {
        val appBarMediator = LocalAppBarMediator.current
        appBarMediator.title = "Курс"
        appBarMediator.content = {
            if (allowEdit) {
                IconButton(onClick = component::onOpenTopicsClick) {
                    Icon(
                        imageVector = Icons.Outlined.ViewDay,
                        contentDescription = "open course topics"
                    )
                }
                IconButton(onClick = component::onCourseEditClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "open course editor"
                    )
                }
            }
        }
        CourseContent(
            course = course,
            allowEdit = allowEdit,
            children = children,
            sidebarChild = sidebarChild,
            onEditCourseClick = component::onCourseEditClick,
            onOpenTopicsClick = component::onOpenTopicsClick
        )
    }
}


@Composable
fun CourseContent(
    course: Resource<CourseResponse>,
    allowEdit: Boolean,
    children: List<CourseComponent.TabChild>,
    sidebarChild: ChildSlot<CourseComponent.SidebarConfig, CourseComponent.SidebarChild>,
    onEditCourseClick: () -> Unit,
    onOpenTopicsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        var selectedTab by remember { mutableStateOf(0) }

        Column {
            TabRow(selectedTabIndex = selectedTab,
                modifier = Modifier.width(396.dp),
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
                divider = {}) {
                children.forEachIndexed { index, item ->
                    Tab(
                        selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(item.title) }
                    )
                }
            }
            Divider()
            Row {
                Box(modifier = Modifier.weight(3f)) {
                    when (val child = children[selectedTab]) {
                        is CourseComponent.TabChild.Elements -> TODO()
                        is CourseComponent.TabChild.Members -> TODO()
                        is CourseComponent.TabChild.Timetable -> TODO()
                    }
                }
                sidebarChild.child?.instance?.let { sidebar ->
                    Box(Modifier.weight(1f)) {
                        when (sidebar) {
//                            is CourseComponent.SidebarChild.ScopeMemberEditor -> TODO()
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

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