package com.denchic45.kts.ui.course.submissions

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.model.Task
import com.denchic45.kts.domain.usecase.FindCourseWorkSubmissionsUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SubmissionsViewModel @Inject constructor(
    @Named(SubmissionsFragment.TASK_ID) private val taskId: String,
    findCourseWorkSubmissionsUseCase: FindCourseWorkSubmissionsUseCase
) : BaseViewModel() {

    val openSubmission = SingleLiveData<Pair<String, String>>()

    fun onSubmissionClick(position: Int) {
        viewModelScope.launch {
            openSubmission.value =
                showSubmissions.first()[position].contentId to showSubmissions.first()[position].student.id
        }
    }

    val showSubmissions: SharedFlow<List<Task.Submission>> =
        findCourseWorkSubmissionsUseCase(taskId).shareIn(
            viewModelScope,
            SharingStarted.Lazily, 1
        )


}