package com.denchic45.kts.ui.studygroup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.NotFound
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

class StudyGroupViewModel @Inject constructor(
    @Named(StudyGroupFragment.GROUP_ID)
     val _studyGroupId: String,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase
) : BaseViewModel() {

     val studyGroupId = _studyGroupId.toUUID()

    private val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = studyGroupId,
                capabilities = listOf(Capability.WriteStudyGroup)
            )
        )
    }.onEach { resource ->
        resource.onSuccess {
            if (it.hasCapability(Capability.WriteStudyGroup)) {
                initTabs.setValue(3)
            } else {
                initTabs.setValue(2)
            }
        }
    }.stateInResource(viewModelScope)

    val initTabs = MutableLiveData(2)

    val menuItemVisibility = SingleLiveData<Pair<Int, Boolean>>()

    val openGroupEditor = SingleLiveData<String>()

    val studyGroup = flow { emit(findStudyGroupByIdUseCase(studyGroupId)) }.onEach {


        it.onSuccess { response ->
            toolbarTitle = response.name
        }.onFailure { failure ->
            if (failure is NotFound)
                finish()
        }
    }.stateInResource(viewModelScope)

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
            R.id.option_edit_group -> openGroupEditor.setValue(_studyGroupId)
        }
    }

    fun onPageSelect(position: Int) {
        if (position == 0 || position == 1) {
            toolbarTitle = toolbarTitle
        }
    }

    companion object {
        const val ALLOW_EDIT_GROUP = "ALLOW_EDIT_GROUP"
        const val PAGE_GROUP_USERS = 0
        const val PAGE_GROUP_SUBJECTS = 1
    }
}