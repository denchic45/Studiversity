package com.denchic45.kts.ui

import com.denchic45.kts.di.AppScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class ToolbarInteractor {
    val title = MutableStateFlow<String?>(null)
}