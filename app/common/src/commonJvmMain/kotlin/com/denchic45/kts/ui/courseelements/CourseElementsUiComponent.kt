package com.denchic45.kts.ui.courseelements

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.NoConnection
import com.denchic45.kts.data.domain.NotFound
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.FindCourseElementsUseCase
import com.denchic45.kts.domain.usecase.FindSelfUserUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.model.HeaderItem
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseElementsUiComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    findSelfUserUseCase: FindSelfUserUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onCreateElement: () -> Unit,
    @Assisted
    private val onOpenElement: (elementId: UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val appBarState = MutableStateFlow(AppBarState(visible = false))

    private val _elements = flow { emit(findCourseElementsUseCase(courseId)) }
        .stateInResource(componentScope)

    val elements = _elements.mapResource {
        buildList {
            it.map { (key, value) ->
                key?.let {
                    add(HeaderItem(key.name))
                }
                addAll(value)
            }
        }
    }.stateInResource(componentScope)

    val course = flow { emit(findCourseByIdUseCase(courseId)) }.stateInResource(componentScope)

        fun onFabClick() {
        onCreateElement()
    }

    fun onItemClick(position: Int) {
        elements.value.onSuccess {
            val task = it[position] as CourseElementResponse
            onOpenElement(task.id)
        }
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