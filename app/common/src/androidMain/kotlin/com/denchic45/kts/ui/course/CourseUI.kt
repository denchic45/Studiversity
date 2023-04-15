package com.denchic45.kts.ui.course

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.courseelements.CourseElementUI
import com.denchic45.kts.ui.courseelements.CourseUiComponent
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.ui.fab.FabState
import com.denchic45.kts.ui.get
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import java.util.UUID


@Composable
fun CourseScreen(
    component: CourseUiComponent,
    fabInteractor: FabInteractor,
) {
    component.lifecycle.apply {
        doOnStart {
            fabInteractor.set(
                FabState(
                    icon = UiIcon.Resource(R.drawable.ic_add),
                    onClick = component::onFabClick
                )
            )
        }
        doOnDestroy {
            fabInteractor.update { it.copy(visible = false) }
        }
    }

    val course by component.course.collectAsState()
    val elements by component.elements.collectAsState()

    val appBarState by component.appBarState.collectAsState()
    CourseContent(
        appBarState = appBarState,
        elements = elements,
        onElementClick = component::onItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseContent(
    appBarState: AppBarState,
    elements: Resource<List<Pair<TopicResponse?, List<CourseElementResponse>>>>,
    onElementClick: (courseId: UUID, elementId: UUID) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = appBarState.title.get(LocalContext.current)) },
                actions = {  },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        elements.onSuccess {
            LazyColumn(contentPadding = paddingValues) {
                it.forEach { (topic, elements) ->
                    topic?.let {
                        item(key = { it.id }) { }
                    }

                    items(elements, key = { it.id }) {
                        CourseElementUI(
                            response = it,
                            onClick = { onElementClick(it.courseId, it.id) })
                    }
                }
            }
        }.onLoading {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}