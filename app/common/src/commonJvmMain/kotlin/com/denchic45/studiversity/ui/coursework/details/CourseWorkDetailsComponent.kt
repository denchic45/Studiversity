package com.denchic45.studiversity.ui.coursework.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.domain.model.Attachment2
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkUseCase
import com.denchic45.studiversity.ui.attachments.AttachmentsComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.AttachmentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseWorkDetailsComponent(
    private val findCourseWorkUseCase: FindCourseWorkUseCase,
    findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    attachmentsComponent: (
        attachments: Flow<Resource<List<Attachment2>>>,
        onAddAttachment: ((AttachmentRequest) -> Unit)?,
        onRemoveAttachment: ((UUID) -> Unit)?,
        ComponentContext
    ) -> AttachmentsComponent,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val courseWork = flow { emit(findCourseWorkUseCase(elementId)) }
        .stateInResource(componentScope, SharingStarted.Eagerly)

    val attachmentsComponent = attachmentsComponent(
        findCourseWorkAttachmentsUseCase(elementId), null, null, childContext("Attachments")
    )
}