package com.denchic45.kts.ui.studygroup.courses

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.usecase.FindCoursesByGroupUseCase
import com.denchic45.kts.ui.model.toGroupCourseItem
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class GroupCoursesComponent(
    findCoursesByGroupUseCase: FindCoursesByGroupUseCase,
    componentContext: ComponentContext,
    _groupId: String,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val groupId = _groupId.toUUID()

    val courses = flow {
        emit(findCoursesByGroupUseCase(groupId).map { list -> list.map(CourseResponse::toGroupCourseItem) })
    }
        .stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)
}