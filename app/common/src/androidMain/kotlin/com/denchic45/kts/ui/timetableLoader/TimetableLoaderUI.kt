package com.denchic45.kts.ui.timetableLoader

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.ui.appbar2.AppBarContent
import com.denchic45.kts.ui.appbar2.LocalAppBarState

@Composable
fun TimetableLoaderScreen(
    component: TimetableLoaderComponent
) {
    val appBarState = LocalAppBarState.current
    component.lifecycle.doOnStart {
        appBarState.content = AppBarContent()
    }
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