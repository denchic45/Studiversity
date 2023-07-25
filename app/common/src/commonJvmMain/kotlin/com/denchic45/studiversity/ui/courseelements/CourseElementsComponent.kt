package com.denchic45.studiversity.ui.courseelements

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCourseElementsUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseElementsComponent(
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val onElementOpen: (courseId: UUID, elementId: UUID, type: CourseElementType) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val refreshing = MutableStateFlow(false)

    private val _elements = flow { emit(findCourseElementsUseCase(courseId)) }
        .stateInResource(componentScope)

    val elements = MutableStateFlow<Resource<List<Pair<CourseTopicResponse?, List<CourseElementResponse>>>>>(
            resourceOf()
        )

    init {
        componentScope.launch {
            elements.emitAll(_elements)
        }
    }

    fun onItemClick(elementId: UUID, type: CourseElementType) {
        onElementOpen(courseId, elementId, type)
    }

    fun onItemLongClick(position: Int) {}

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

    fun onRefresh() {
        componentScope.launch {
            refreshing.update { true }
            elements.update { findCourseElementsUseCase(courseId) }
            refreshing.update { false }
        }
    }
}