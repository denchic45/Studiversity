package com.denchic45.kts.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.util.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface RootComponent<C : Any, T : Any> {

    val navigation: StackNavigation<C>

    val childStack: Value<ChildStack<C, T>>

    fun hasBackStack(): Flow<Boolean> {
       return childStack.asFlow().map { it.backStack.isNotEmpty() }
    }
}

//abstract class RootComponentDelegate<C : Parcelable, T : Any>(
//    private val componentContext: ComponentContext,
//    val initialConfig: C,
//    val factory: (configuration: C, ComponentContext) -> T,
//) : ComponentContext by componentContext {
//
//    val navigation: StackNavigation<C> = StackNavigation()
//
//    val childStack: Value<ChildStack<C, T>> = childStack<C, T>(
//        source = navigation,
//        initialConfiguration = initialConfig,
//        childFactory = factory
//    )
//
//    val hasBackStack: Flow<Boolean> = childStack.asFlow().map { it.backStack.isNotEmpty() }
//}