package com.denchic45.kts.ui.appbar2

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.denchic45.kts.ui.appbar.AppBarInteractor

@OptIn(ExperimentalMaterial3Api::class)
class AppBarState2(
    private val scrollBehavior: TopAppBarScrollBehavior,
    appBarContent: AppBarContent
) {
    private var _content by mutableStateOf(appBarContent)

    var content: AppBarContent
        get() = _content
        set(value) {
            _content = value
            expand()
        }

    fun expand() {
        scrollBehavior.state.heightOffset = 0f
    }

    fun hide() {
        scrollBehavior.state.heightOffset = -Float.MAX_VALUE
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppBarState(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    appBarContent: AppBarContent = AppBarContent()
) = remember { AppBarState2(scrollBehavior, appBarContent) }

val LocalAppBarState = staticCompositionLocalOf<AppBarState2> { error("Nothing AppBarState") }