package com.denchic45.studiversity.ui.coursetopics

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.usecase.AddCourseTopicUseCase
import com.denchic45.studiversity.domain.usecase.ObserveCourseTopicsUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseTopicUseCase
import com.denchic45.studiversity.domain.usecase.UpdateCourseTopicUseCase
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.swap
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.UpdateCourseTopicRequest
import com.denchic45.stuiversity.util.optPropertyOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseTopicsComponent(
    observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    private val addCourseTopicUseCase: AddCourseTopicUseCase,
    val updateCourseTopicUseCase: UpdateCourseTopicUseCase,
    val removeCourseTopicUseCase: RemoveCourseTopicUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    private var oldPosition = -1
    private var position = -1

    private val observedTopics = observeCourseTopicsUseCase(courseId)
    val topics = MutableStateFlow<Resource<List<CourseTopicResponse>>>(resourceOf())

    init {
        componentScope.launch {
            observedTopics.collect {
                topics.emit(it)
            }
        }
    }

    fun onTopicMove(oldPos: Int, newPos: Int) {
        if (this.oldPosition == -1)
            this.oldPosition = oldPos
        this.position = newPos

        topics.value.onSuccess { list ->
            componentScope.launch {
                topics.emit(resourceOf(list.swap(oldPos, newPos)))
            }
        }
    }


    fun onTopicReorder(topicId: UUID, oldPos: Int, newPos: Int) {
        if (oldPos == newPos) return

    }

    fun onTopicAdd(name: String) {
        if (name.isEmpty()) return
        componentScope.launch {
            addCourseTopicUseCase(CreateCourseTopicRequest(courseId, name))
        }
    }

    fun onTopicRename(position: Int, name: String) {
        topics.value.onSuccess { list ->
            componentScope.launch {
                updateCourseTopicUseCase(
                    list[position].id,
                    UpdateCourseTopicRequest(courseId,optPropertyOf(name))
                )
            }
        }
    }

    fun onTopicRemove(position: Int) {
        topics.value.onSuccess { list ->
            componentScope.launch {
                removeCourseTopicUseCase(
                    courseId,
                    list[position].id,
                    RelatedTopicElements.CLEAR_TOPIC
                )
            }
        }
    }

    fun onTopicMoved() {
        if (oldPosition == position)
            return

        componentScope.launch {
            topics.value.onSuccess { topics ->
//              val prevOrder = if (position == 0) 0 else topics[position - 1].order
//
//              val nextOrder =
//                  if (position == topics.size - 1) topics[position - 1].order + (1024 * 2)
//                  else topics[position + 1].order
//
//              updateCourseTopicUseCase(
//                  topics.toMutableList().apply {
//                      set(
//                          position,
//                          topics[position].copy(
//                              order = Orders.getBetweenOrders(
//                                  prevOrder,
//                                  nextOrder
//                              )
//                          )
//                      )
//                  }
//              )
            }
            oldPosition = -1
            position = -1
        }
    }

}