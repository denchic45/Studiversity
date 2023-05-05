package com.denchic45.kts.ui.timetableLoader

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children

@Composable
fun TimetableLoaderScreen(component: TimetableLoaderComponent) {
    Children(stack = component.childStack) {
        when (val instance = it.instance) {
            is TimetableLoaderComponent.TimetableLoaderChild.Creator -> {
                TimetableCreatorScreen(instance.component)
            }
            is TimetableLoaderComponent.TimetableLoaderChild.Publisher -> {
                TimetablesPublisherScreen(instance.component)
            }
        }
    }
}