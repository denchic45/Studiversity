package com.denchic45.studiversity.ui.timetableloader

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children

@Composable
fun TimetableLoaderScreen(
    component: TimetableLoaderComponent,
) {
    Children(stack = component.childStack) {
        when (val instance = it.instance) {
            is TimetableLoaderComponent.TimetableLoaderChild.Creator -> {
                TimetableCreatorDialog(instance.component, component::onDismissRequest)
            }

            is TimetableLoaderComponent.TimetableLoaderChild.Publisher -> {
                TimetablesPublisherScreen(instance.component)
            }
        }
    }
}