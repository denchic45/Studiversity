package com.denchic45.kts.ui.group

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.AppBarMediator
import com.denchic45.kts.ui.components.Tab
import com.denchic45.kts.ui.components.TabIndicator
import com.denchic45.kts.ui.group.courses.GroupCoursesScreen
import com.denchic45.kts.ui.group.members.GroupMembersScreen
import com.denchic45.kts.ui.navigation.GroupChild
import com.denchic45.kts.ui.navigation.GroupTabsChild
import com.denchic45.kts.ui.navigation.GroupTabsConfig

@Composable
fun GroupScreen(appBarMediator: AppBarMediator, groupRootComponent: GroupRootComponent) {
    appBarMediator.title = "Группа"
    Card(shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
        elevation = 0.dp) {
        val stack by groupRootComponent.stack.subscribeAsState()

        when (val child = stack.active.instance) {
            is GroupChild -> GroupContent(child.groupComponent)
        }
    }
}

@Composable
fun GroupContent(groupComponent: GroupComponent) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val selected by groupComponent.selectedTab.collectAsState()
        TabRow(selectedTabIndex = selected,
            modifier = Modifier.width(396.dp).height(56.dp),
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selected])) },
            divider = {}) {
            val tabs by groupComponent.tabs.collectAsState()
            tabs.forEachIndexed { index, item ->
                Tab(selected == index,
                    onClick = { groupComponent.onTabClick(index) },
                    text = item.title)
            }
        }
        Divider()

        val childStack by groupComponent.stack.subscribeAsState()

        when (val child = childStack.active.instance) {
            is GroupTabsChild.Members -> GroupMembersScreen(child.membersComponent)
            is GroupTabsChild.Courses -> GroupCoursesScreen(child.coursesComponent)
        }
    }
}
