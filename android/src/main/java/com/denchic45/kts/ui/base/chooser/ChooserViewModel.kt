package com.denchic45.kts.ui.base.chooser

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.domain.error.NetworkError
import com.denchic45.kts.domain.error.NotFound
import com.denchic45.kts.domain.error.SearchError
import com.denchic45.kts.ui.adminPanel.finder.SearchState
import com.denchic45.kts.ui.base.BaseViewModel
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class ChooserViewModel<T : DomainModel> : BaseViewModel() {

    private val typeName: MutableSharedFlow<String> = MutableSharedFlow()

    val items: StateFlow<SearchState<out T>> =
        typeName.flatMapLatest { name ->
            sourceFlow(name).map(Result<List<T>, SearchError>::toSearchState)
        }.stateIn(viewModelScope, SharingStarted.Lazily, SearchState.Loading)

    protected abstract val sourceFlow: (String) -> Flow<Result<List<T>, SearchError>>

    fun onItemClick(position: Int) {
        onItemSelect((items.value as SearchState.Found).items[position])
    }

    abstract fun onItemSelect(item: T)

    fun onNameType(typedName: String) {
        viewModelScope.launch { typeName.emit(typedName) }
    }
}

fun <T> Result<List<T>, SearchError>.toSearchState(): SearchState<out T> = mapBoth(
    success = { SearchState.Found(it) },
    failure = {
        when (it) {
            NetworkError -> SearchState.NoConnection
            NotFound -> SearchState.NotFound
        }
    }
)
