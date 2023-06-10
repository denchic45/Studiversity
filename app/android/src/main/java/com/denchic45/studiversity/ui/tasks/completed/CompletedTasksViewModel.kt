package com.denchic45.studiversity.ui.tasks.completed

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.domain.usecase.FindYourSubmittedCourseWorksUseCase
import com.denchic45.studiversity.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class CompletedTasksViewModel @Inject constructor(
    findYourSubmittedCourseWorksUseCase: FindYourSubmittedCourseWorksUseCase
) : BaseViewModel() {
    val tasks = flow { emit(findYourSubmittedCourseWorksUseCase()) }.shareIn(
        viewModelScope,
        SharingStarted.Lazily
    )
}