package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.FindCourseByContainsNameUseCase
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class CourseSearchComponent(
    private val findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase,
    @Assisted
    override val onSelect: (CourseResponse) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : SearchComponent<CourseResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<CourseResponse>>> {
        return findCourseByContainsNameUseCase(query)
    }
}