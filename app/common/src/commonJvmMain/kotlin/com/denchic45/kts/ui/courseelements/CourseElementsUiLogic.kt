package com.denchic45.kts.ui.courseelements

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.FindCourseElementsUseCase
import com.denchic45.kts.domain.usecase.FindSelfUserUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.kts.ui.model.CourseElementItem
import com.denchic45.kts.ui.model.HeaderItem
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.flow
import java.util.*

abstract class CourseElementsUiLogic(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
    private val findCourseElementsUseCase: FindCourseElementsUseCase,
    findSelfUserUseCase: FindSelfUserUseCase,
    private val courseId: UUID,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val _elements = flow { emit(findCourseElementsUseCase(courseId)) }
        .stateInResource(componentScope)

    val elements = _elements.mapResource {
        buildList {
            it.map { (key, value) ->
                key?.let {
                    add(HeaderItem(key.name))
                }
                addAll(value.map {
                    CourseElementItem(
                        it.id,
                        it.name
                    )
                })
            }
        }
    }.stateInResource(componentScope)

    fun onItemClick(position: Int) {
        elements.value.onSuccess {
            val task = it[position] as CourseElementItem
            onOpenElement(task.id)
        }
    }

    fun onTaskItemLongClick(position: Int) {}

    abstract fun onCreateElement()

    abstract fun onOpenElement(elementId: UUID)
}