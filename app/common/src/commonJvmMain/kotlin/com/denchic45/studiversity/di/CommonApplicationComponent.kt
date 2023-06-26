package com.denchic45.studiversity.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.ui.AppNavigation
import com.denchic45.studiversity.ui.MainComponent
import com.denchic45.studiversity.ui.root.RootComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@AppScope
abstract class CommonApplicationComponent(

) {

    @AppScope
    @Provides
    protected fun appNavigation(): AppNavigation = StackNavigation()

    abstract val mainComponent: (ComponentContext) -> MainComponent

    abstract val rootComponent: (ComponentContext) -> RootComponent
}