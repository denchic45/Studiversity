package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

interface SearchableComponent<T> {
    val query: MutableStateFlow<String>
//    val foundItems: StateFlow<Resource<List<T>>>

    val searchState: StateFlow<SearchState<T>>

    fun onItemClick(item: T)
    fun onQueryChange(text: String)
}

abstract class ChooserComponent<T>(componentContext: ComponentContext) :
    ComponentContext by componentContext, SearchableComponent<T> {

    protected abstract val onSelect: (T) -> Unit

    final override val query = MutableStateFlow("")

    private val coroutineScope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    val foundItems: StateFlow<Resource<List<T>>> = query
        .flatMapLatest {
            if (it.isNotEmpty()) search(it.trim())
            else flowOf(resourceOf(emptyList()))
        }
        .stateIn(coroutineScope, SharingStarted.Lazily, Resource.Success(emptyList()))

    final override val searchState: StateFlow<SearchState<T>> =
        combine(query, foundItems) { query, items ->
            if (query.isEmpty())
                SearchState.EmptyQuery
            else
                SearchState.Result(items)
        }.stateIn(coroutineScope, SharingStarted.Lazily, SearchState.EmptyQuery)


    protected abstract fun search(query: String): Flow<Resource<List<T>>>

    override fun onItemClick(item: T) {
        onSelect(item)
    }

    override fun onQueryChange(text: String) {
        query.value = text
    }
}

sealed class SearchState<out T> {
    object EmptyQuery : SearchState<Nothing>()
    data class Result<T>(val items: Resource<List<T>>) : SearchState<T>()
}