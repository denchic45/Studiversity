package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class SearchableComponent<T>(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    protected abstract val onFinish: (T?) -> Unit
    protected val componentScope = componentScope()

    private val forceTrigger = Channel<Unit>()
    val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val foundItems: StateFlow<Resource<List<T>>> =
        forceTrigger.receiveAsFlow()
            .onStart { emit(Unit) }
            .flatMapLatest { query }
            .flatMapLatest {
                val text = it.trim()
                if (text.isNotEmpty()) search(text)
                else flowOf(resourceOf(emptyList()))
            }
            .stateIn(componentScope, SharingStarted.Lazily, resourceOf(emptyList()))

    val searchState: StateFlow<SearchState<T>> = combine(query, foundItems) { query, items ->
        if (query.isEmpty()) SearchState.EmptyQuery
        else SearchState.Result(items)
    }.stateIn(componentScope, SharingStarted.Lazily, SearchState.EmptyQuery)

    protected abstract fun search(query: String): Flow<Resource<List<T>>>

    fun onItemClick(item: T) {
        onFinish(item)
    }

    fun onQueryChange(text: String) {
        query.value = text
    }

    fun forceUpdate() {
        componentScope.launch {
            forceTrigger.send(Unit)
        }
    }

    fun onDismissRequest() {
        onFinish(null)
    }
}

sealed class SearchState<out T> {
    data object EmptyQuery : SearchState<Nothing>()
    data class Result<T>(val items: Resource<List<T>>) : SearchState<T>()
}