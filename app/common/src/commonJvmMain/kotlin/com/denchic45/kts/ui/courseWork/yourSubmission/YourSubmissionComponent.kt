package com.denchic45.kts.ui.courseWork.yourSubmission

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourSubmissionUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourSubmissionComponent(
    private val findYourSubmissionUseCase: FindYourSubmissionUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
):ComponentContext by componentContext {

    private val componentScope = componentScope()

    val yourSubmission = flow { emit(findYourSubmissionUseCase(courseId, workId)) }
        .stateInResource(componentScope)
}