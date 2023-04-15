package com.denchic45.kts.ui.courseelements

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.FindCourseElementsUseCase
import com.denchic45.kts.domain.usecase.FindSelfUserUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseUiComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    findSelfUserUseCase: FindSelfUserUseCase,
    private val appBarInteractor: AppBarInteractor,
    private val fabInteractor: FabInteractor,
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

    val appBarState = MutableStateFlow(AppBarState())

    val elements = flow { emit(findCourseElementsUseCase(courseId)) }
        .stateInResource(componentScope)

    val course = flow { emit(findCourseByIdUseCase(courseId)) }.stateInResource(componentScope)

    init {
        lifecycle.doOnStart {
            appBarInteractor.set(AppBarState(visible = false))
            course.filterSuccess().onEach {
                appBarState.value = AppBarState(title = uiTextOf(it.value.name))
            }.launchIn(componentScope)

        }
    }

    fun onFabClick() {
        onCourseElementEditorOpen()
    }

    fun onItemClick(courseId: UUID, elementId: UUID) {
        onElementOpen(courseId, elementId)
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