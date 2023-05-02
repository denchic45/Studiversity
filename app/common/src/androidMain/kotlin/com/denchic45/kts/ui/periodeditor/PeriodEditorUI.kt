package com.denchic45.kts.ui.periodeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess

@Composable
fun PeriodEditorScreen(component: PeriodEditorComponent) {
    val stateResource by component.state.collectAsState()
    Column {

        PeriodEditorContent(stateResource = stateResource)
    }
}

@Composable
fun PeriodEditorContent(stateResource: Resource<EditingPeriod>) {
    stateResource.onSuccess { state ->

    }
}