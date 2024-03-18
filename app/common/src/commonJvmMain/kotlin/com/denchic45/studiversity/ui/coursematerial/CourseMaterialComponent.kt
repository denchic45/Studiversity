package com.denchic45.studiversity.ui.coursematerial

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseMaterialAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseMaterialUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.toAttachmentItems
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseMaterialComponent(
    private val findCourseMaterialUseCase: FindCourseMaterialUseCase,
    findCourseMaterialAttachmentsUseCase: FindCourseMaterialAttachmentsUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val onEditorOpen: (courseId: UUID, elementId: UUID?) -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val elementId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val observedCourseMaterial = flow {
        emit(findCourseMaterialUseCase( elementId))
    }.stateInResource(componentScope, SharingStarted.Eagerly)

    val courseMaterial = MutableStateFlow<Resource<CourseMaterialResponse>>(resourceOf())

    val refreshing = MutableStateFlow(false)

    init {
        componentScope.launch {
            courseMaterial.emitAll(observedCourseMaterial)
        }
    }

    fun onRefresh() {
        componentScope.launch {
            refreshing.update { true }
            courseMaterial.update { findCourseMaterialUseCase( elementId) }
            refreshing.update { false }
        }
    }

    val attachments = findCourseMaterialAttachmentsUseCase( elementId)
        .mapResource { it.toAttachmentItems() }
        .stateInResource(componentScope, SharingStarted.Eagerly)

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(
            Capability.WriteCourseElements,
        )
    ).stateInResource(componentScope)

    val allowEditMaterial = capabilities.map {
        it.takeValueIfSuccess()?.hasCapability(Capability.WriteCourseElements) ?: false
    }

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    fun onAttachmentClick(item: AttachmentItem) {
        when (item) {
            is AttachmentItem.FileAttachmentItem -> when (item.state) {
                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                FileState.FailDownload, FileState.Preview -> componentScope.launch {
                    downloadFileUseCase(item.attachmentId)
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }

    fun onEditClick() {
        onEditorOpen(courseId, elementId)
    }

    fun onDeleteClick() {
        confirmDialogInteractor.set(
            ConfirmState(
                title = uiTextOf("Удалить материал?"),
                text = uiTextOf("Прикрепленные файлы также будут удалены")
            )
        )

        componentScope.launch {
            if (confirmDialogInteractor.receiveConfirm()) {
                removeCourseElementUseCase(courseId, elementId).onSuccess {
                    withContext(Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
        }
    }
}