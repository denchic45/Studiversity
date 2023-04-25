package com.denchic45.kts.ui.coursework.details

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindCourseWorkUseCase
import com.denchic45.kts.ui.model.toAttachmentItems
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkDetailsComponent(
    private val findCourseWorkUseCase: FindCourseWorkUseCase,
    private val findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val courseWork = flow { emit(findCourseWorkUseCase(courseId, elementId)) }
        .stateInResource(componentScope)
    val attachments = findCourseWorkAttachmentsUseCase(courseId, elementId)
        .mapResource { it.toAttachmentItems() }
        .stateInResource(componentScope)
}