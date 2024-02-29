package com.denchic45.studiversity.ui.navigation

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
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
    val overlayNavigation: SlotNavigation<C>

    val childSlot: Value<ChildSlot<C, T>>

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSlot.isActiveFlow()
    }
}

interface EmptyChildrenContainer : ChildrenContainer {

    override fun hasChildrenFlow(): Flow<Boolean> {
        return flowOf(false)
    }
}