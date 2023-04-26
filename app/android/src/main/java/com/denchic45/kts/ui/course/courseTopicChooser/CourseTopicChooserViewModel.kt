package com.denchic45.kts.ui.course.courseTopicChooser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.ObserveCourseTopicsUseCase
import com.denchic45.kts.ui.course.workEditor.CourseWorkEditorFragment
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class CourseTopicChooserViewModel @Inject constructor(
    observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    @Named(CourseWorkEditorFragment.COURSE_ID) _courseId: String
) : ViewModel() {

    private val courseId = _courseId.toUUID()

    private val _topics = observeCourseTopicsUseCase(courseId).stateInResource(viewModelScope)

    val topics = _topics.mapResource { topics ->
        listOf("Без темы") + topics.map { it.name }
    }

    private val _selectedTopic = Channel<TopicResponse?>()
    val selectedTopic = _selectedTopic.receiveAsFlow()

    val selectedTopicPosition = MutableSharedFlow<Int>(replay = 1)

    fun onSectionItemClick(position: Int) {
        selectedTopicPosition.tryEmit(position)
    }

    fun onSaveClick() {
        _topics.value.onSuccess { topics ->
            viewModelScope.launch {
                val selectedPos = selectedTopicPosition.first()

                _selectedTopic.send(
                    if (selectedPos == 0) null
                    else topics[selectedPos - 1]
                )
            }
        }
    }
}