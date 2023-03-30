package com.denchic45.kts.ui

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.CoroutineScope

interface BaseUiComponent {
    val componentScope: CoroutineScope
}

 class BaseUiComponentDelegate(
    componentContext: ComponentContext,
) : BaseUiComponent, ComponentContext by componentContext {

    override val componentScope = componentScope()
}