package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.AppBarMediator
import com.denchic45.kts.ui.components.TabIndicator
import com.denchic45.kts.ui.studygroup.courses.GroupCoursesScreen
import com.denchic45.kts.ui.studygroup.members.StudyGroupMembersScreen
import com.denchic45.kts.ui.theme.toDrawablePath


@Composable
fun StudyGroupScreen(
    component: StudyGroupComponent,
    appBarMediator: AppBarMediator
) {

    val studyGroupResource by component.studyGroup.collectAsState()
    val selectedTab by component.selectedTab.collectAsState()

    appBarMediator.title = "Группа"
    appBarMediator.content = {
        Spacer(Modifier.weight(1f))
//        IconButton(onClick = component::onAddStudentClick) {
//            Icon(
//                imageVector = Icons.Rounded.Add,
//                tint = Color.DarkGray,
//                contentDescription = ""
//            )
//        }
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource("ic_settings".toDrawablePath()),
                tint = Color.DarkGray,
                contentDescription = ""
            )
        }
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
        elevation = 0.dp
    ) {
        studyGroupResource.onSuccess { studyGroup ->
            StudyGroupContent(
                selectedTab = selectedTab,
                children = component.childTabs,
                onTabSelect = component::onTabSelect
            )
        }.onLoading {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun StudyGroupContent(
    selectedTab: Int,
    children: List<StudyGroupComponent.TabChild>,
    onTabSelect: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TabRow(selectedTabIndex = selectedTab,
            modifier = Modifier.width(396.dp).height(56.dp),
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
            divider = {}) {
//            val tabs by response.tabs.collectAsState()
            children.forEachIndexed { index, item ->
                Tab(
                    selectedTab == index,
                    onClick = { onTabSelect(index) },
                    text = { Text(item.title)}
                )
            }
        }
        Divider()
        when (val child = children[selectedTab]) {
            is StudyGroupComponent.TabChild.Members -> StudyGroupMembersScreen(child.component)
            is StudyGroupComponent.TabChild.Courses -> GroupCoursesScreen(child.component)
            is StudyGroupComponent.TabChild.Timetable -> TODO()
        }
    }
}
