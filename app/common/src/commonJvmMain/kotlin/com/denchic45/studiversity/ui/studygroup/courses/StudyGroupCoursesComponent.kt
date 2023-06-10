package com.denchic45.studiversity.ui.studygroup.courses

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.map
import com.denchic45.studiversity.domain.usecase.FindCoursesByGroupUseCase
import com.denchic45.studiversity.ui.model.toGroupCourseItem
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupCoursesComponent(
    findCoursesByGroupUseCase: FindCoursesByGroupUseCase,
    @Assisted
    private val onCourseOpen: (UUID) -> Unit,
    @Assisted
    studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val courses = flow {
        emit(findCoursesByGroupUseCase(studyGroupId).map { list -> list.map(CourseResponse::toGroupCourseItem) })
    }.stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)

    fun onCourseClick(courseId: UUID) {
        onCourseOpen(courseId)
    }
}