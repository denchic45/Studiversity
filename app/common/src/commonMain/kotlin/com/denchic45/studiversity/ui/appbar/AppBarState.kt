package com.denchic45.studiversity.ui.appbar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class AppBarState(
    scrollBehavior: TopAppBarScrollBehavior,
    appBarContent: AppBarContent,
    private val snapAnimationSpec: AnimationSpec<Float>,
    private val flingAnimationSpec: DecayAnimationSpec<Float>,
) {
    var content by mutableStateOf(appBarContent)

    var scrollBehavior: TopAppBarScrollBehavior by mutableStateOf(scrollBehavior)

    private val isExpanded
        get() = scrollBehavior.state.heightOffset == 0f


    fun update(block: AppBarState.() -> Unit) {
        block()
        expand()
    }

    var navigationIcon by mutableStateOf(NavigationIcon.TOGGLE)

    fun expand() {
        if (!isExpanded)
            scrollBehavior.state.heightOffset = 0f
    }

    fun hide() {
        if (isExpanded)
            scrollBehavior.state.heightOffset = -Float.MAX_VALUE
    }

    suspend fun animateExpand() {
        if (!isExpanded)
            animateScroll(0f)
    }

    suspend fun animateHide() {
        if (isExpanded)
            animateScroll(scrollBehavior.state.heightOffsetLimit)
    }

    private suspend fun animateScroll(targetValue: Float) {
        val velocity = 300f
//        val flingAnimationSpec = scrollBehavior.flingAnimationSpec
//        val snapAnimationSpec = scrollBehavior.snapAnimationSpec
        val state = scrollBehavior.state

        // In case there is an initial velocity that was left after a previous user fling, animate to
        // continue the motion to expand or collapse the app bar.
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        ).animateDecay(flingAnimationSpec) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset
            state.heightOffset = initialHeightOffset + delta
            lastValue = value
        }
        // Snap if animation specs were provided.
        AnimationState(initialValue = state.heightOffset).animateTo(
            targetValue,
            animationSpec = snapAnimationSpec
        ) { state.heightOffset = value }


    }
}

enum class NavigationIcon { NOTHING, TOGGLE, BACK }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppBarState(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    appBarContent: AppBarContent = AppBarContent(),
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
) = remember { AppBarState(scrollBehavior, appBarContent, snapAnimationSpec, flingAnimationSpec) }

val LocalAppBarState = compositionLocalOf<AppBarState> { error("Nothing AppBarState") }

@Composable
fun hideAppBar() {
    LocalAppBarState.current.let { appBarState ->
        remember(Unit) {
            appBarState.hide()
        }
    }
}

@Composable
fun expandAppBar() {
    LocalAppBarState.current.let { appBarState ->
        remember(Unit) {
            appBarState.expand()
        }
    }
}

@Composable
fun animateHideAppBar() {
    LocalAppBarState.current.let { appBarState ->
        LaunchedEffect(Unit) {
            appBarState.animateHide()
        }
    }
}

@Composable
fun updateAppBarState(content: AppBarContent) {
    updateAppBarState(Unit, content = content)
}

@Composable
fun updateAppBarState(key1: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    remember(key1) {
        updateState(content, appBarState)
    }
}

@Composable
fun updateAppBarState(key1: Any?, key2: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    remember(key1, key2) {
        updateState(content, appBarState)
    }
}

@Composable
fun updateAppBarState(key1: Any?, key2: Any?, key3: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    remember(key1, key2, key3) {
        updateState(content, appBarState)
    }
}

@Composable
fun updateAppBarState(vararg keys: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    remember(*keys) {
        updateState(content, appBarState)
    }
}

private fun updateState(
    content: AppBarContent,
    appBarState: AppBarState,
) {
    appBarState.content = content
    appBarState.expand()
}

@Composable
fun updateAnimatedAppBarState(content: AppBarContent) {
    updateAnimatedAppBarState(Unit, content = content)
}

@Composable
fun updateAnimatedAppBarState(key1: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    val coroutineScope = rememberCoroutineScope()
    remember(key1) {
        updateAnimatedState(appBarState, content, coroutineScope)
    }
}

@Composable
fun updateAnimatedAppBarState(key1: Any?, key2: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    val coroutineScope = rememberCoroutineScope()
    remember(key1, key2) {
        updateAnimatedState(appBarState, content, coroutineScope)
    }
}

@Composable
fun updateAnimatedAppBarState(vararg keys: Any?, content: AppBarContent) {
    val appBarState = LocalAppBarState.current
    val coroutineScope = rememberCoroutineScope()
    remember(*keys) {
        updateAnimatedState(appBarState, content, coroutineScope)
    }
}

private fun updateAnimatedState(
    appBarState: AppBarState,
    content: AppBarContent,
    coroutineScope: CoroutineScope,
): Job {
    appBarState.content = content
    return coroutineScope.launch {
        appBarState.animateExpand()
    }
}