package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

interface SearchableComponent<T> {
    val query: MutableStateFlow<String>
    val foundItems: StateFlow<Resource<List<T>>>
    fun onItemClick(item: T)
    fun onQueryChange(text: String)
}

abstract class ChooserComponent<T>(componentContext: ComponentContext) :
    ComponentContext by componentContext, SearchableComponent<T> {

    protected abstract val onSelect: (T) -> Unit

    final override val query = MutableStateFlow("")

    private val coroutineScope = componentScope()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val foundItems: StateFlow<Resource<List<T>>> = query.filter(String::isNotEmpty)
        .flatMapLatest(::search)
        .stateIn(coroutineScope, SharingStarted.Lazily, Resource.Success(emptyList()))


    protected abstract fun search(query: String): Flow<Resource<List<T>>>

    override fun onItemClick(item: T) {
        onSelect(item)
    }

    override fun onQueryChange(text: String) {
        query.value = text
    }
}