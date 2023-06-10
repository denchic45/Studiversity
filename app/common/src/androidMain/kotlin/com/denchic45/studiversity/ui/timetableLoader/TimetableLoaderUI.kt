package com.denchic45.studiversity.ui.timetableLoader

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.LocalAppBarState

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