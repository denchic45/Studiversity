package com.denchic45.kts.di

import com.denchic45.kts.ui.MainComponent
import com.denchic45.kts.ui.login.LoginComponent
import com.denchic45.kts.ui.splash.SplashComponent

interface LogicComponent {

    val splashComponent: SplashComponent

    val mainComponent: () -> MainComponent

    val loginComponent: () -> LoginComponent
}