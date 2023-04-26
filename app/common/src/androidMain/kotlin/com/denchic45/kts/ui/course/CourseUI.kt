package com.denchic45.kts.ui.course

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.courseelements.CourseElementsScreen
import com.denchic45.kts.ui.coursemembers.CourseMembersScreen
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.ui.fab.FabState
import com.denchic45.stuiversity.api.course.model.CourseResponse


@Composable
fun CourseScreen(
    component: CourseComponent,
    fabInteractor: FabInteractor,
    appBarInteractor: AppBarInteractor
) {
    component.lifecycle.apply {
        doOnStart {
            appBarInteractor.set(AppBarState(visible = false))
            fabInteractor.set(
                FabState(
                    icon = UiIcon.Resource(R.drawable.ic_add),
                    onClick = component::onFabClick
                )
            )
        }
        doOnStop {
            appBarInteractor.set(AppBarState(visible = true))
            fabInteractor.update { it.copy(visible = false) }
        }
    }

    val course by component.course.collectAsState()
    val allowEdit by component.allowEdit.collectAsState(false)
    val children by component.children.collectAsState()

    CourseContent(
        course = course,
        allowEdit = allowEdit,
        childrenResource = children,
        onCourseEditClick = component::onCourseEditClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CourseContent(
    course: Resource<CourseResponse>,
    allowEdit: Boolean,
    childrenResource: Resource<List<CourseComponent.Child>>,
    onCourseEditClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    course.onSuccess {
                        Text(text = it.name)
                    }
                },
                actions = {
                    if (allowEdit)
                        IconButton(onClick = { onCourseEditClick() }) {
                            Icon(Icons.Outlined.Settings, "Edit Course")
                        }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        childrenResource.onSuccess {children->
            HorizontalPager(pageCount = children.size) {
                when (val child = children[it]) {
                    is CourseComponent.Child.Elements -> CourseElementsScreen(
                        component = child.component,
                        contentPadding = paddingValues
                    )

                    is CourseComponent.Child.Members -> CourseMembersScreen(
                        component = child.component,
                        contentPadding = paddingValues
                    )
                }
            }
        }
    }
}