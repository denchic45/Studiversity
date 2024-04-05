package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.map
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AttachStudyGroupsToCourseUseCase
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindCoursesByGroupUseCase
import com.denchic45.studiversity.ui.model.toGroupCourseItem
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.search.CourseChooserComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupCoursesComponent(
    findCoursesByGroupUseCase: FindCoursesByGroupUseCase,
    attachStudyGroupsToCourseUseCase: AttachStudyGroupsToCourseUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val courseChooserComponent: ((CourseItem?) -> Unit, ComponentContext) -> CourseChooserComponent,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = studyGroupId,
        capabilities = listOf(Capability.WriteStudyGroup)
    ).stateInResource(componentScope)

    val allowEdit = capabilities.mapResource {
        it.hasCapability(Capability.WriteStudyGroup)
    }.stateInResource(componentScope)

    val slotNavigation = SlotNavigation<SlotConfig>()
    val childSlot = childSlot(
        source = slotNavigation,
        childFactory = { config, context ->
            when (config) {
                SlotConfig.CourseChooser -> SlotChild.CourseChooser(courseChooserComponent({ course ->
                    course?.let { attachStudyGroupsToCourseUseCase(course.id, studyGroupId) }
                }, context))
            }
        }
    )

    val courses = flow {
        emit(findCoursesByGroupUseCase(studyGroupId).map { it.map(CourseResponse::toGroupCourseItem) })
    }.stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)

    fun onCourseClick(courseId: UUID) {
        rootNavigator.push(RootConfig.Course(courseId))
    }

    fun onAddCourseClick() {
        slotNavigation.activate(SlotConfig.CourseChooser)
    }

    @Parcelize
    sealed class SlotConfig : Parcelable {
        data object CourseChooser : SlotConfig() {
            private fun readResolve(): Any = CourseChooser
        }
    }

    sealed class SlotChild {
        class CourseChooser(val component: CourseChooserComponent) : SlotChild()
    }
}