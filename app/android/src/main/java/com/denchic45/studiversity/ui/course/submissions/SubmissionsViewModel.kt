package com.denchic45.studiversity.ui.course.submissions

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCourseWorkSubmissionsUseCase
import com.denchic45.studiversity.ui.base.BaseViewModel
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubmissionsViewModel(
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val taskId: UUID,

) : BaseViewModel() {



    val openSubmission = SingleLiveData<Triple<String, String, String>>()


}