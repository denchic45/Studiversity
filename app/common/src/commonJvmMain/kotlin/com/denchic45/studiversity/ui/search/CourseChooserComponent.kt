package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.usecase.FindCourseByContainsNameUseCase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class CourseChooserComponent(
    private val findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase,
    @Assisted
    override val onFinish: (CourseItem?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : SearchableComponent<CourseItem>(componentContext) {
    override fun search(query: String): Flow<Resource<List<CourseItem>>> {
        return findCourseByContainsNameUseCase(query).mapResource { it.map(CourseResponse::toItem) }
    }
}