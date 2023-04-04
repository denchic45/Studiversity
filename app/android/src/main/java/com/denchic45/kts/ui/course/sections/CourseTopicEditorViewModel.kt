package com.denchic45.kts.ui.course.sections

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.AddCourseTopicUseCase
import com.denchic45.kts.domain.usecase.ObserveCourseTopicsUseCase
import com.denchic45.kts.domain.usecase.RemoveCourseTopicUseCase
import com.denchic45.kts.domain.usecase.UpdateCourseTopicUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class CourseTopicEditorViewModel @Inject constructor(
    @Named(CourseTopicEditorFragment.COURSE_ID)
    private val _courseId: String,
    observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    private val addCourseTopicUseCase: AddCourseTopicUseCase,
    val updateCourseTopicUseCase: UpdateCourseTopicUseCase,
    val removeCourseTopicUseCase: RemoveCourseTopicUseCase
) : BaseViewModel() {

    private val courseId = _courseId.toUUID()
    private var oldPosition = -1
    private var position = -1

    val topics = MutableSharedFlow<List<TopicResponse>>(replay = 1)

    init {
        viewModelScope.launch {
            observeCourseTopicsUseCase(courseId).collect {
                it.onSuccess { responses -> topics.emit(responses) }
            }
        }
    }

    fun onSectionMove(oldPosition: Int, position: Int) {
        if (this.oldPosition == -1)
            this.oldPosition = oldPosition
        this.position = position

        viewModelScope.launch {
            Collections.swap(topics.first(), oldPosition, position)
            topics.emit(topics.first())
        }
    }

//    fun onSectionMoved() {
//        if (oldPosition == position)
//            return
//
//        viewModelScope.launch {
//            val topics: List<TopicResponse> = topics.first()
//
//            val prevOrder = if (position == 0) 0 else topics[position - 1].order
//
//            val nextOrder =
//                if (position == topics.size - 1) topics[position - 1].order + (1024 * 2)
//                else topics[position + 1].order
//
//            updateCourseTopicUseCase(
//                topics.toMutableList().apply {
//                    set(
//                        position,
//                        topics[position].copy(
//                            order = Orders.getBetweenOrders(
//                                prevOrder,
//                                nextOrder
//                            )
//                        )
//                    )
//                }
//            )
//        }
//        oldPosition = -1
//        position = -1
//    }

    fun onSectionAdd(name: String) {
        if (name.isEmpty()) return
        viewModelScope.launch {
            addCourseTopicUseCase(courseId, CreateTopicRequest(name))
        }
    }

    fun onSectionRename(name: String, position: Int) {
        viewModelScope.launch {
            updateCourseTopicUseCase(
                courseId,
                topics.first()[position].id,
                UpdateTopicRequest(optPropertyOf(name))
            )
        }
    }

    fun onSectionRemove(position: Int) {
        viewModelScope.launch {
            removeCourseTopicUseCase(
                courseId,
                topics.first()[position].id,
                RelatedTopicElements.CLEAR_TOPIC
            )
        }
    }

}