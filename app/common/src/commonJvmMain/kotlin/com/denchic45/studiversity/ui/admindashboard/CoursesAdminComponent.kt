package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.push
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.usecase.ArchiveCourseUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseUseCase
import com.denchic45.studiversity.domain.usecase.UnarchiveCourseUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.CourseChooserComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CoursesAdminComponent(
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val archiveCourseUseCase: ArchiveCourseUseCase,
    private val unarchiveCourseUseCase: UnarchiveCourseUseCase,
    private val removeCourseUseCase: RemoveCourseUseCase,
    courseChooserComponent: (onSelect: (CourseResponse) -> Unit, ComponentContext) -> CourseChooserComponent,
    courseEditorComponent: (onFinish: () -> Unit, UUID?, ComponentContext) -> CourseEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SearchableAdminComponent<CourseResponse> {

    private val componentScope = componentScope()

    override val chooserComponent: CourseChooserComponent =
        courseChooserComponent(::onSelect, componentContext)

//    private val overlayNavigation = OverlayNavigation<AddCourseConfig>()
//    val childOverlay = childOverlay(source = overlayNavigation,
//        handleBackButton = true,
//        childFactory = { _, context ->
//            AddCourseChild(courseEditorComponent(overlayNavigation::dismiss, null, context))
//        })

    override fun onSelect(item: CourseResponse) {
        rootNavigation.bringToFront(RootConfig.Course(item.id))
    }

    override fun onAddClick() {
        rootNavigation.push(RootConfig.CourseEditor(null))
    }

    override fun onEditClick(id: UUID) {
        rootNavigation.push(RootConfig.CourseEditor(id))
    }

    fun onArchiveClick(id: UUID) {
        componentScope.launch {
            chooserComponent.foundItems.value.onSuccess { courses ->
                val confirm = confirmDialogInteractor.confirmRequest(
                    ConfirmState(
                        uiTextOf("Архивировать курс \"${courses.find { it.id == id }?.name}\"?"),
                        uiTextOf("Курсы, находящиеся в архиве, недоступны для преподавателей и учащихся. Чтобы они могли работать с таким курсом, его нужно восстановить. Этот курс будет перемещен в архив.")
                    )
                )
                if (confirm) {
                    archiveCourseUseCase(id)
                }
            }
        }
    }

    fun onUnarchiveClick(id: UUID) {
        componentScope.launch {
            chooserComponent.foundItems.value.onSuccess { courses ->
                val confirm = confirmDialogInteractor.confirmRequest(
                    ConfirmState(
                        uiTextOf("Восстановить курс \"${courses.find { it.id == id }?.name}\"?"),
                        uiTextOf("Курс вновь будет доступен для преподавателей и учащихся, также будет отображаться в меню приложения.")
                    )
                )
                if (confirm) {
                    unarchiveCourseUseCase(id)
                }
            }
        }
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            chooserComponent.foundItems.value.onSuccess { courses ->
                val confirm = confirmDialogInteractor.confirmRequest(
                    ConfirmState(
                        uiTextOf("Удалить курс \"${courses.find { it.id == id }?.name}\"?"),
                        uiTextOf("Вы лишитесь доступа ко всем элементам этого курса. Это действие нельзя отменить.")
                    )
                )
                if (confirm) {
                    removeCourseUseCase(id)
                }
            }
        }
    }
}