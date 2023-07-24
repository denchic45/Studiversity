package com.denchic45.studiversity.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.denchic45.studiversity.ui.AppBarMediator
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.theme.DesktopApp

@Composable
fun ApplicationScope.SplashWindow(backDispatcher: BackDispatcher) {
    DesktopApp(
        title = "Studiversity",
        onCloseRequest = ::exitApplication,
        backDispatcher = backDispatcher
    ) {
        CompositionLocalProvider(LocalAppBarMediator provides AppBarMediator()) {
            Box(Modifier.size(200.dp))
        }
    }
}