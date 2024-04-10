package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.denchic45.studiversity.api.course.element.model.CourseElementResponse
import com.denchic45.studiversity.api.course.element.model.CourseWork
import com.denchic45.studiversity.api.course.work.model.CourseWorkType
import com.denchic45.studiversity.ui.theme.AppTheme
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseElementsScreen(component: CourseElementsComponent) {
    val elementsResource by component.elements.collectAsState()
    val refreshing by component.refreshing.collectAsState()

    val refreshState = rememberPullRefreshState(refreshing, component::onRefresh)

    Box(modifier = Modifier.pullRefresh(refreshState)) {
        CourseElementsContent(
            elementsResource = elementsResource,
            onElementClick = component::onItemClick,
        )
        if (refreshState.progress > 0)
            PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Preview
@Composable
fun CourseElementPreview() {
    AppTheme {
        CourseElementListItem(
            CourseElementResponse(
                id = UUID.randomUUID(),
                courseId = UUID.randomUUID(),
                name = "Задание №25",
                description = "Сегодня",
                null,
                0,
                CourseWork(LocalDate.now(), LocalTime.now(), CourseWorkType.ASSIGNMENT, 5, null)
            )
        ) {}
    }
}