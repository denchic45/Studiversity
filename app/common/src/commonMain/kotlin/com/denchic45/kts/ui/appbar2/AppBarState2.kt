package com.denchic45.kts.ui.appbar2

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
class AppBarState2(
    val scrollBehavior: TopAppBarScrollBehavior,
    appBarContent: AppBarContent,
    private val snapAnimationSpec: AnimationSpec<Float>,
    private val flingAnimationSpec: DecayAnimationSpec<Float>,
) {
    private var _content by mutableStateOf(appBarContent)

    var content: AppBarContent
        get() = _content
        set(value) {
            _content = value
            expand()
        }

    var navigationIcon by mutableStateOf(NavigationIcon.TOGGLE)

    fun expand() {
        scrollBehavior.state.heightOffset = 0f
    }

    fun hide() {
        scrollBehavior.state.heightOffset = -Float.MAX_VALUE
    }

    suspend fun animateExpand() = animateScroll(0f)

    suspend fun animateHide() = animateScroll(scrollBehavior.state.heightOffsetLimit)

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
        AnimationState(initialValue = state.heightOffset).animateTo<Float, AnimationVector1D>(
            targetValue,
            animationSpec = snapAnimationSpec
        ) { state.heightOffset = value }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun settleAppBar(
    state: TopAppBarState,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?,
    velocity: Float = 300f,
) {
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }
}

enum class NavigationIcon { TOGGLE, BACK }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppBarState(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    appBarContent: AppBarContent = AppBarContent(),
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
) = remember { AppBarState2(scrollBehavior, appBarContent, snapAnimationSpec, flingAnimationSpec) }

val LocalAppBarState = staticCompositionLocalOf<AppBarState2> { error("Nothing AppBarState") }