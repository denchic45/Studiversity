package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.FindCourseByContainsNameUseCase
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class CourseChooserComponent(
    private val findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase,
    @Assisted
    override val onFinish: (CourseResponse?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<CourseResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<CourseResponse>>> {
        return findCourseByContainsNameUseCase(query)
    }
}