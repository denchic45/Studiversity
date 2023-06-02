package com.denchic45.kts.ui.navigation

import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.denchic45.kts.ui.navigator.RootChild
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.util.asFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

interface RootStackChildrenContainer : StackChildrenContainer<RootConfig, RootChild> {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun hasChildrenFlow(): Flow<Boolean> {
        return childStack.asFlow().flatMapLatest { childStack ->
            val instance = childStack.active.instance
            if (instance is ChildrenContainerChild) {
                instance.component.hasChildrenFlow()
                    .map { hasBackStackFromActiveChild ->
                        childStack.hasBackStack() || hasBackStackFromActiveChild
                    }
            } else {
                flowOf(childStack.hasBackStack())
            }
        }
    }
}

interface ChildrenContainerChild {
    val component: ChildrenContainer
}

fun <C : Any, T : Any> Value<ChildStack<C, T>>.hasBackStackFlow(): Flow<Boolean> {
    return asFlow().map { it.hasBackStack() }
}

fun <C : Any, T : Any> Value<ChildOverlay<C, T>>.isActiveFlow(): Flow<Boolean> {
    return asFlow().map { it.isActive() }
}

fun <C : Any, T : Any> ChildStack<C, T>.hasBackStack(): Boolean {
    return backStack.isNotEmpty()
}

fun <C : Any, T : Any> ChildOverlay<C, T>.isActive(): Boolean {
    return overlay != null
}