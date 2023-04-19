package com.denchic45.kts.ui.course.submissions

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindCourseWorkSubmissionsUseCase
import com.denchic45.kts.ui.base.BaseViewModel
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