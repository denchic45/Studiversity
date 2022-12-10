package com.denchic45.kts.ui.groupChooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.usecase.FindGroupByContainsNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import me.tatarka.inject.annotations.Inject

@Inject
class GroupChooserComponent(
    private val findGroupByContainsNameUseCase: FindGroupByContainsNameUseCase,
    componentContext: ComponentContext,
    onSelect: (groupId: String) -> Unit
) : ComponentContext by componentContext {

    private val typedText = MutableStateFlow("")
    val items = typedText.flatMapLatest { findGroupByContainsNameUseCase(it) }
}