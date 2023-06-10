package com.denchic45.studiversity.ui.tasks.overdue

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.domain.usecase.FindOverdueTasksForYourGroupUseCase
import com.denchic45.studiversity.ui.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class OverdueTasksViewModel @Inject constructor(
    findOverdueTasksForYourGroupUseCase: FindOverdueTasksForYourGroupUseCase
) : BaseViewModel() {

    val works = flow { emit(findOverdueTasksForYourGroupUseCase()) }.shareIn(
        viewModelScope,
        SharingStarted.Lazily
    )

}