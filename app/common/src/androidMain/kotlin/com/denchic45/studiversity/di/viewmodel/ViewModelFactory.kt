package com.denchic45.studiversity.di.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.tatarka.inject.annotations.Inject

@Inject
class ViewModelFactory<VM : ViewModel> constructor(
    private val viewModel: ()->VM
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel() as T
    }
}