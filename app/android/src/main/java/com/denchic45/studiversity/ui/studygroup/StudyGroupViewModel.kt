package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.R
import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.data.domain.NotFound
import com.denchic45.studiversity.domain.onFailure
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.studiversity.ui.AndroidUiComponent
import com.denchic45.studiversity.ui.AndroidUiComponentDelegate
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class StudyGroupViewModel(
    @Assisted
    val _studyGroupId: String,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    @Assisted
    private val componentContext: ComponentContext,
) : AndroidUiComponent by AndroidUiComponentDelegate(componentContext) {

    private val studyGroupId = _studyGroupId.toUUID()

    private val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = studyGroupId,
        capabilities = listOf(Capability.WriteStudyGroup)
    ).stateInResource(componentScope)

    val tabs = MutableStateFlow<List<String>>(emptyList())

    val menuItemVisibility = SingleLiveData<Pair<Int, Boolean>>()

    val studyGroup = findStudyGroupByIdUseCase(studyGroupId).stateInResource(componentScope)

    init {
        componentScope.launch {
            studyGroup.collect {
                it.onSuccess { response -> }
                    .onFailure { failure ->
                        if (failure is NotFound)
                            finish()
                    }
            }
        }
        componentScope.launch {
            capabilities.collect { resource ->
                resource.onSuccess {
                    if (it.hasCapability(Capability.WriteStudyGroup)) {
                        tabs.value = listOf("Участники", "Курсы", "Расписание")
                    } else {
                        tabs.value = listOf("Участники", "Курсы")
                    }
                }
            }
        }
    }

    fun onPrepareOptions(currentItem: Int) {
        capabilities.value.onSuccess {
            menuItemVisibility.value = Pair(
                R.id.option_edit_group,
                it.hasCapability(Capability.WriteStudyGroup)
            )
            when (currentItem) {
                PAGE_GROUP_USERS -> {
//                menuItemVisibility.setValue(
//                    Pair(
//                        R.id.option_add_student, uiPermissions.isAllowed(
//                            ALLOW_EDIT_GROUP
//                        )
//                    )
//                )
                }

                PAGE_GROUP_SUBJECTS -> {
//                menuItemVisibility.setValue(Pair(R.id.option_add_student, false))
                }
            }
        }
    }

    override fun onOptionClick(itemId: Int) {
        when (itemId) {
//            R.id.option_edit_group -> openGroupEditor.setValue(_studyGroupId)
        }
    }

    fun onPageSelect(position: Int) {
//        if (position == 0 || position == 1) {
//            toolbarTitle = toolbarTitle
//        }
    }

    companion object {
        const val ALLOW_EDIT_GROUP = "ALLOW_EDIT_GROUP"
        const val PAGE_GROUP_USERS = 0
        const val PAGE_GROUP_SUBJECTS = 1
    }
}