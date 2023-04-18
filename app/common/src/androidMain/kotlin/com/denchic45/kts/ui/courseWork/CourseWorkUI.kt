package com.denchic45.kts.ui.courseWork

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.courseWork.details.CourseWorkDetailsScreen
import com.denchic45.kts.ui.courseWork.submissions.CourseWorkSubmissionsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CourseWorkScreen(component: CourseWorkComponent) {
    val childrenResource by component.children.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    childrenResource.onSuccess { children ->
        val pagerState = rememberPagerState()

        Column {
            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                // Add tabs for all of our pages
                children.forEachIndexed { index, child ->
                    Tab(
                        text = { Text(child.title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }


            HorizontalPager(state = pagerState, pageCount = children.size) {
               Box(modifier = Modifier.fillMaxSize()) {
                   when (val child = children[it]) {
                       is CourseWorkComponent.Child.Details -> CourseWorkDetailsScreen(child.component)
                       is CourseWorkComponent.Child.Submissions -> CourseWorkSubmissionsScreen(child.component)
                   }
               }
            }
        }
    }

}