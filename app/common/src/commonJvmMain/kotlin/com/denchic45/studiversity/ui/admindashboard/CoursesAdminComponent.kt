package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.usecase.ArchiveCourseUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseUseCase
import com.denchic45.studiversity.domain.usecase.UnarchiveCourseUseCase
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.search.CourseChooserComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CoursesAdminComponent(
    private val archiveCourseUseCase: ArchiveCourseUseCase,
    private val unarchiveCourseUseCase: UnarchiveCourseUseCase,
    private val removeCourseUseCase: RemoveCourseUseCase,
    courseChooserComponent: (
        onSelect: (CourseItem?) -> Unit,
        ComponentContext
    ) -> CourseChooserComponent,
    courseEditorComponent: (
        onFinish: (Boolean) -> Unit,
        UUID?,
        ComponentContext
    ) -> CourseEditorComponent,
    @Assisted
    private val rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    AdminSearchableComponent<CourseItem> {

    private val componentScope = componentScope()

    private val slotNavigation = SlotNavigation<Config>()
    val childSlot: Value<ChildSlot<Config, Child>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is Config.CourseEditor -> Child.CourseEditor(
                    courseEditorComponent({ edited ->
                        slotNavigation.dismiss()
                        if (edited)
                            searchableComponent.forceUpdate()
                    }, config.courseId, context)
                )
            }
        })

    override val searchableComponent: CourseChooserComponent = courseChooserComponent(
        { course -> course?.let { onSelect(course) } }, componentContext
    )

    override fun onSelect(item: CourseItem) {
        rootNavigation.bringToFront(RootConfig.Course(item.id))
    }

    override fun onAddClick() {
        slotNavigation.activate(Config.CourseEditor(null))
    }

    override fun onEditClick(id: UUID) {
        slotNavigation.activate(Config.CourseEditor(id))
    }

    fun onArchiveClick(id: UUID) {
        componentScope.launch {
            archiveCourseUseCase(id).onSuccess { searchableComponent.forceUpdate() }
        }
    }

    fun onUnarchiveClick(id: UUID) {
        componentScope.launch {
            unarchiveCourseUseCase(id).onSuccess { searchableComponent.forceUpdate() }
        }
    }

    override fun onRemoveClick(id: UUID) {
        componentScope.launch {
            removeCourseUseCase(id).onSuccess { searchableComponent.forceUpdate() }
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        data class CourseEditor(val courseId: UUID?) : Config
    }

    sealed interface Child {
        class CourseEditor(val component: CourseEditorComponent) : Child
    }
}