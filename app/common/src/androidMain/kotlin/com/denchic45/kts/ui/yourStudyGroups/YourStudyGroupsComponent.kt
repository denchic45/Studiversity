package com.denchic45.kts.ui.yourStudyGroups

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.di.AppScope
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class YourStudyGroupsComponent(
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val studyGroups = flow { emit(findYourStudyGroupsUseCase()) }.stateInResource(componentScope)

    val selectedStudyGroupId = MutableStateFlow<String?>(null)

    init {
        componentScope.launch {
            studyGroups.collect {
                it.onSuccess { groups ->
                    selectedStudyGroupId.update {
                        groups.firstOrNull()?.id.toString()
                    }
                }
            }
        }
    }

    fun onGroupSelect(index: Int) {
        studyGroups.value.onSuccess {
            selectedStudyGroupId.value = it[index].id.toString()
        }
    }
}