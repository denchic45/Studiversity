package com.denchic45.kts.ui.courseelements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.FindCourseElementsUseCase
import com.denchic45.kts.domain.usecase.FindSelfUserUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseUiComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    findSelfUserUseCase: FindSelfUserUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onCourseElementEditorOpen: () -> Unit,
    @Assisted
    private val onElementOpen: (courseId: UUID, elementId: UUID) -> Unit,
    @Assisted
    private val onCourseEditorOpen: (courseId: UUID) -> Unit,
    @Assisted
    private val onCourseTopicsOpen: (courseId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

//    val appBarState = MutableStateFlow(AppBarState())

    val elements = flow { emit(findCourseElementsUseCase(courseId)) }
        .stateInResource(componentScope)

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

    fun onFabClick() {
        onCourseElementEditorOpen()
    }

    fun onItemClick(elementId: UUID) {
        onElementOpen(courseId, elementId)
    }

    fun onCourseEditClick() {
        onCourseEditorOpen(courseId)
    }

    fun onTaskItemLongClick(position: Int) {}

    fun onContentMove(oldPosition: Int, position: Int) {
////        if (this.oldPosition == -1)
////            this.oldPosition = oldPosition
////        this.position = position
////        Collections.swap(showContents.value!!, oldPosition, position)
////
////        showContents.value = showContents.value
    }

    //
    fun onContentMoved() {
////        if (oldPosition == position) return
////
////        componentScope.launch {
////            val contents = showContents.value!!
////
////            val prevOrder: Int = if (position == 0 || contents[position - 1] !is CourseContent) 0
////            else (contents[position - 1] as CourseContent).order.toInt()
////
////            val nextOrder: Int =
////                if (position == contents.size - 1) ((contents[position - 1] as CourseContent).order + (1024 * 2)).toInt()
////                else (contents[position + 1] as CourseContent).order.toInt()
////
////            updateCourseContentOrderUseCase(
////                contents[position].id,
////                Orders.getBetweenOrders(prevOrder, nextOrder)
////            )
////            oldPosition = -1
////            position = -1
////        }
    }
}