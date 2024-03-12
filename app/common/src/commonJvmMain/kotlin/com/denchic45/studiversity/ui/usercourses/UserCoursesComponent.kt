package com.denchic45.studiversity.ui.usercourses

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCoursesUseCase
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.util.uuidOf
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UserCoursesComponent(
    findCoursesUseCase: FindCoursesUseCase,
    @Assisted
    private val onResult: (UUID?) -> Unit,
    @Assisted
    private val userId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()
    val coursesByUser = findCoursesUseCase(memberId = uuidOf(userId)).stateInResource(componentScope)

    fun onCourseClick(id: UUID) {
        onResult(id)
    }

    fun onDismissRequest() {
        onResult(null)
    }
}