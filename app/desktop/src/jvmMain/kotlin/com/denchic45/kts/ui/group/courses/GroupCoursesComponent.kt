package com.denchic45.kts.ui.group.courses

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.model.CourseHeader
import com.denchic45.kts.domain.usecase.ObserveCoursesByGroupUseCase
import com.denchic45.kts.ui.model.toGroupCourseItem
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
class GroupCoursesComponent(
    observeCoursesByGroupUseCase: ObserveCoursesByGroupUseCase,
    componentContext: ComponentContext,
    groupId: String,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val courses =
        observeCoursesByGroupUseCase(groupId).map { list -> list.map(CourseHeader::toGroupCourseItem) }
            .stateIn(componentScope, SharingStarted.Lazily, emptyList())
}