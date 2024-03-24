package com.denchic45.studiversity.ui.userstudygroups

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindStudyGroupsUseCase
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.util.userIdOf
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class UserStudyGroupsComponent(
    findStudyGroupsUseCase: FindStudyGroupsUseCase,
    @Assisted
    private val onResult: (UUID?) -> Unit,
    @Assisted
    private val userId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()
    val studyGroupsByUser =
        findStudyGroupsUseCase(memberId = userIdOf(userId)).stateInResource(componentScope)

    fun onCourseClick(id: UUID) {
        onResult(id)
    }

    fun onDismissRequest() {
        onResult(null)
    }
}