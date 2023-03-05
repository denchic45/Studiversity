package com.denchic45.kts.ui.course.sectionPicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.domain.model.Section
import com.denchic45.kts.domain.usecase.FindCourseTopicsUseCase
import com.denchic45.kts.ui.course.taskEditor.CourseWorkEditorFragment
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SectionPickerViewModel @Inject constructor(
    findCourseTopicsUseCase: FindCourseTopicsUseCase,
    @Named(CourseWorkEditorFragment.COURSE_ID) _courseId: String
) : ViewModel() {

    private val courseId = _courseId.toUUID()

    private val _selectedSection = Channel<TopicResponse>()
    val selectedSectionId = _selectedSection.receiveAsFlow()

    var selectedTopic:TopicResponse? = null
        set(value) {
            field = value
            postSectionPosition()
        }

    val selectedTopicPosition = MutableLiveData(0)

    val sections = MutableLiveData<List<String>>()
    private var _topics: List<TopicResponse> = emptyList()

    fun onSectionItemClick(position: Int) {
        selectedTopic = if (position == 0) null
            else _topics[position - 1]
        selectedTopicPosition.value = position
    }

    init {
        viewModelScope.launch {
            findCourseTopicsUseCase(courseId).collect { topics ->
                _topics = topics
                sections.value = listOf("Без секции") + topics.map { it.name }
            }
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            _selectedSection.send(selectedTopic)
        }
    }

    private fun postSectionPosition() {
        selectedTopicPosition.value =
            if (selectedTopic == Section.createEmpty()) {
                0
            } else {
                _topics.indexOfFirst { it == selectedTopic } + 1
            }
    }
}