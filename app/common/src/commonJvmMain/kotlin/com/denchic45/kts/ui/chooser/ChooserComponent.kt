package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class ChooserComponent<T>(componentContext: ComponentContext) : ComponentContext by componentContext {

    abstract val onFinish: (T?)->Unit

    private val typeName: MutableSharedFlow<String> = MutableSharedFlow()

    protected val componentScope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: StateFlow<Resource<List<T>>> = typeName.flatMapLatest { name ->
        search(name)
    }.stateIn(componentScope, SharingStarted.Lazily, Resource.Loading)


    protected abstract fun search(query:String):Flow<Resource<List<T>>>

    private val backCallback = BackCallback { onFinish(null) }

    init {
        backHandler.register(backCallback)
    }

    fun onItemClick(position: Int) {
        items.value.onSuccess {
            componentScope.launch {
                onFinish(it[position])
            }
        }
    }

    fun onNameType(typedName: String) {
        componentScope.launch { typeName.emit(typedName) }
    }
}