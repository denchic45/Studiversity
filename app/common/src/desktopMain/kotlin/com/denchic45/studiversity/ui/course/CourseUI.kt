package com.denchic45.studiversity.ui.course

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewDay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.course.model.CourseResponse

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
//                appBarInteractor.set(AppBarState(visible = false))
//                fabInteractor.set(
//                    FabState(
//                        icon = UiIcon.Resource(R.drawable.ic_add),
//                        onClick = component::onFabClick
//                    )
//                )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CourseContent(
    course: Resource<CourseResponse>,
    allowEdit: Boolean,
    children: List<CourseComponent.TabChild>,
    sidebarChild: ChildOverlay<CourseComponent.SidebarConfig, CourseComponent.SidebarChild>,
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
                sidebarChild.overlay?.instance?.let { sidebar ->
                    Box(Modifier.weight(1f)) {
                        when (sidebar) {
                            is CourseComponent.SidebarChild.Profile -> TODO()
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