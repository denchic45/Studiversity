package com.denchic45.kts.di

import com.denchic45.kts.ui.login.LoginComponent
import com.denchic45.kts.ui.root.RootComponent
import com.denchic45.kts.ui.splash.SplashComponent
import com.denchic45.kts.ui.timetable.TimetableComponent

interface LogicComponent {

    val splashComponent: SplashComponent

    val rootComponent: () -> RootComponent

    val loginComponent: () -> LoginComponent

//    val timetableComponent: TimetableComponent
}