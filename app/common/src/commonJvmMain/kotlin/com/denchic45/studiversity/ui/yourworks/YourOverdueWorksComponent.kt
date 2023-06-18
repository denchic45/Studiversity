package com.denchic45.studiversity.ui.yourworks

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindYourOverdueWorksUseCase
import com.denchic45.studiversity.domain.usecase.FindYourUpcomingWorksUseCase
import com.denchic45.studiversity.util.componentScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourOverdueWorksComponent(
    findYourOverdueWorksUseCase: FindYourOverdueWorksUseCase,
    @Assisted
    private val onWorkOpen: (courseId:UUID, workId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    val componentScope = componentScope()
    val works = findYourOverdueWorksUseCase().stateInResource(componentScope)

    fun onWorkClick(courseId:UUID, workId: UUID) {
        onWorkOpen(courseId,workId)
    }
}