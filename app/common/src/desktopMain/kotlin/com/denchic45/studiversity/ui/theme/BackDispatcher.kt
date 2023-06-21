package com.denchic45.studiversity.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.WindowState
import com.arkivanov.essenty.backhandler.BackDispatcher

val LocalBackDispatcher = staticCompositionLocalOf<BackDispatcher> {
    error("CompositionLocal LocalBackDispatcher not present")
}