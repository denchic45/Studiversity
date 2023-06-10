package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.usecase.FindCourseByContainsNameUseCase
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class CourseChooserComponent(
    private val findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase,
    @Assisted
    override val onSelect: (CourseResponse) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<CourseResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<CourseResponse>>> {
        return findCourseByContainsNameUseCase(query)
    }
}