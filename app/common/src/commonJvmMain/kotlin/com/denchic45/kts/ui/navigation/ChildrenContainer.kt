package com.denchic45.kts.ui.navigation

import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface ChildrenContainer {
    fun hasChildrenFlow(): Flow<Boolean>
}

interface StackChildrenContainer<C : Any, T : Any> : ChildrenContainer {
    val navigation: StackNavigation<C>

    val childStack: Value<ChildStack<C, T>>

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childStack.hasBackStackFlow()
    }
}

interface OverlayChildrenContainer<C : Any, T : Any> : ChildrenContainer {
    val overlayNavigation: OverlayNavigation<C>

    val childOverlay: Value<ChildOverlay<C, T>>

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childOverlay.isActiveFlow()
    }
}

interface EmptyChildrenContainer : ChildrenContainer {

    override fun hasChildrenFlow(): Flow<Boolean> {
        return flowOf(false)
    }
}