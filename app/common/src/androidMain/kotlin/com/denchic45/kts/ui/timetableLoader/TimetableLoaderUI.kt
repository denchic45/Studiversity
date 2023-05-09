package com.denchic45.kts.ui.timetableLoader

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState

@Composable
fun TimetableLoaderScreen(
    component: TimetableLoaderComponent,
    appBarInteractor: AppBarInteractor
) {
    component.lifecycle.doOnStart {
        appBarInteractor.set(AppBarState())
    }
    Children(stack = component.childStack) {
        when (val instance = it.instance) {
            is TimetableLoaderComponent.TimetableLoaderChild.Creator -> {
                TimetableCreatorScreen(instance.component)
            }

            is TimetableLoaderComponent.TimetableLoaderChild.Publisher -> {
                TimetablesPublisherScreen(instance.component, appBarInteractor)
            }
        }
    }
}