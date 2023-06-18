package com.denchic45.studiversity.ui.tasks.upcoming

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.domain.usecase.FindYourUpcomingWorksUseCase
import com.denchic45.studiversity.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class UpcomingTasksViewModel @Inject constructor(
    findYourUpcomingWorksUseCase: FindYourUpcomingWorksUseCase
) : BaseViewModel() {

    val tasks = flow { emit(findYourUpcomingWorksUseCase()) }
        .shareIn(viewModelScope, SharingStarted.Lazily)
}