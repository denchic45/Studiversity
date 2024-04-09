package com.denchic45.studiversity.ui.coursematerial

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.coursematerialeditor.CourseMaterialEditorComponent
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.FileState
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseMaterialComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val findCourseMaterialUseCase: FindCourseMaterialUseCase,
    findCourseMaterialAttachmentsUseCase: FindCourseMaterialAttachmentsUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    courseMaterialEditorComponent: (
        onFinish: (CourseMaterialResponse) -> Unit,
        courseId: UUID,
        materialId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseMaterialEditorComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val materialId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val slotNavigation = SlotNavigation<SlotConfig>()
    val childSlot = childSlot(slotNavigation,
        childFactory = { config, context ->
            when (config) {
                SlotConfig.CourseMaterialEditor -> SlotChild.CourseMaterialEditor(
                    courseMaterialEditorComponent(
                        { slotNavigation.dismiss() },
                        courseId,
                        materialId,
                        null,
                        context
                    )
                )
            }
        })

    private val observedCourseMaterial = flow {
        emit(findCourseMaterialUseCase(materialId))
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
            courseMaterial.update { findCourseMaterialUseCase(materialId) }
            refreshing.update { false }
        }
    }

    val attachments = findCourseMaterialAttachmentsUseCase(materialId)
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
                    downloadFileUseCase(item.id)
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }

    fun onEditClick() {
        slotNavigation.activate(SlotConfig.CourseMaterialEditor)
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
                removeCourseElementUseCase(courseId, materialId).onSuccess {
                    withContext(Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
        }
    }

    @Parcelize
    sealed class SlotConfig : Parcelable {
        data object CourseMaterialEditor : SlotConfig() {
            private fun readResolve(): Any = CourseMaterialEditor
        }
    }

    sealed class SlotChild {
        class CourseMaterialEditor(val component: CourseMaterialEditorComponent) : SlotChild()
    }
}