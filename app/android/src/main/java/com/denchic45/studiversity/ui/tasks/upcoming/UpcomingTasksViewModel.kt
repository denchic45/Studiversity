package com.denchic45.studiversity.ui.tasks.upcoming

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.domain.usecase.FindUpcomingTasksForYourGroupUseCase
import com.denchic45.studiversity.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class UpcomingTasksViewModel @Inject constructor(
    findUpcomingTasksForYourGroupUseCase: FindUpcomingTasksForYourGroupUseCase
) : BaseViewModel() {

    val tasks = flow { emit(findUpcomingTasksForYourGroupUseCase()) }
        .shareIn(viewModelScope, SharingStarted.Lazily)
}