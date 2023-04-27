package com.denchic45.kts.ui.course

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.ui.CourseMembersComponent
import com.denchic45.kts.ui.courseelements.CourseElementsComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class CourseComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    _courseElementsComponent: (
        courseId: UUID,
        onElementOpen: (courseId: UUID, elementId: UUID) -> Unit,
        ComponentContext
    ) -> CourseElementsComponent,
    _courseMembersComponent: (
        courseId: UUID,
        onMemberOpen: (memberId: UUID) -> Unit,
        ComponentContext
    ) -> CourseMembersComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onCourseEditorOpen: (courseId: UUID) -> Unit,
    @Assisted
    private val onElementOpen: (courseId: UUID, elementId: UUID) -> Unit,
    @Assisted
    private val onCourseElementEditorOpen: (courseId: UUID, elementId: UUID?) -> Unit,
    @Assisted
    private val onCourseTopicsOpen: (courseId: UUID) -> Unit,
    @Assisted
    private val onMemberOpen: (memberId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val course = flow { emit(findCourseByIdUseCase(courseId)) }.stateInResource(componentScope)

    val allowEdit = flow {
        emit(
            when (val resource = checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.WriteCourse)
            )) {
                Resource.Loading,
                is Resource.Error -> false

                is Resource.Success -> {
                    resource.value.hasCapability(Capability.WriteCourse)
                }
            }
        )
    }

    private val courseElementsComponent = _courseElementsComponent(
        courseId,
        onElementOpen,
        componentContext.childContext("elements")
    )

    private val courseMembersComponent = _courseMembersComponent(
        courseId,
        onMemberOpen,
        componentContext.childContext("members")
    )

    val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf()
            )
        )
    }.shareIn(componentScope, SharingStarted.Lazily)

    private val defaultChildren =
        listOf(Child.Elements(courseElementsComponent), Child.Members(courseMembersComponent))

    val children = capabilities.mapResource {
        defaultChildren
    }.stateInResource(
        scope = componentScope,
        initialValue = Resource.Success(defaultChildren)
    )

    fun onFabClick() {
        onCourseElementEditorOpen(courseId, null)
    }

    fun onCourseEditClick() {
        onCourseEditorOpen(courseId)
    }

    fun onTopicEditClick() {
        onCourseTopicsOpen(courseId)
    }

    sealed class Child {

        abstract val title: String

        class Elements(val component: CourseElementsComponent) : Child() {
            override val title: String = "Элементы"
        }

        class Members(val component: CourseMembersComponent) : Child() {
            override val title: String = "Участники"
        }
    }
}