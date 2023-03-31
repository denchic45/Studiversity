package com.denchic45.kts.ui.timetableEditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TimetableEditorViewModel @Inject constructor(
    val _timetable: TimetableResponse?,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    val viewState = MutableStateFlow(Resource.Loading)

}