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
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
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

    private val _topics = observeCourseTopicsUseCase(courseId)
    val topics = MutableStateFlow<Resource<List<TopicResponse>>>(resourceOf())

    init {
        componentScope.launch {
            _topics.collect {
                topics.emit(it)
            }
        }
    }

    fun onTopicMove(oldPosition: Int, position: Int) {
        if (this.oldPosition == -1)
            this.oldPosition = oldPosition
        this.position = position

        topics.value.onSuccess { list ->
            componentScope.launch {
                topics.emit(resourceOf(list.swap(oldPosition, position)))
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

    fun onTopicReorder(topicId: UUID, order: Int) {

    }

    fun onTopicAdd(name: String) {
        if (name.isEmpty()) return
        componentScope.launch {
            addCourseTopicUseCase(courseId, CreateTopicRequest(name))
        }
    }

    fun onTopicRename(position: Int, name: String) {
        topics.value.onSuccess { list ->
            componentScope.launch {
                updateCourseTopicUseCase(
                    courseId,
                    list[position].id,
                    UpdateTopicRequest(optPropertyOf(name))
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
}