package com.denchic45.studiversity.ui.base.chooser

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.base.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class ChooserViewModel<T> : BaseViewModel() {

    private val typeName: MutableSharedFlow<String> = MutableSharedFlow()

    val items: StateFlow<Resource<List<T>>> = typeName.flatMapLatest { name ->
        sourceFlow(name)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading)

    protected abstract val sourceFlow: (String) -> Flow<Resource<List<T>>>

    abstract fun onItemClick(position: Int)

//    abstract fun onItemSelect(item: T)

    fun onNameType(typedName: String) {
        viewModelScope.launch { typeName.emit(typedName) }
    }
}