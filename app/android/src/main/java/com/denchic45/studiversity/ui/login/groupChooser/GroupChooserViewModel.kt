package com.denchic45.studiversity.ui.login.groupChooser

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByContainsNameUseCase
import com.denchic45.studiversity.ui.base.chooser.ChooserViewModel
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupChooserViewModel @Inject constructor(
    private val groupChooserInteractor: GroupChooserInteractor,
    private val findStudyGroupByContainsNameUseCase: FindStudyGroupByContainsNameUseCase
) : ChooserViewModel<StudyGroupResponse>() {

//    fun onGroupItemClick(position: Int) {
//        viewModelScope.launch {
//            val groupId = _groupAndSpecialtyList.value[position].id
//            groupChooserInteractor.findById(groupId)
//            finish()
//            groupChooserInteractor.postSelectGroupId((_groupAndSpecialtyList.value[position] as GroupHeader))
//        }
//    }

    override val sourceFlow: (String) -> Flow<Resource<List<StudyGroupResponse>>> = {
      findStudyGroupByContainsNameUseCase(it)
    }

    override fun onItemClick(position: Int) {
        items.value.onSuccess {
            viewModelScope.launch {
                groupChooserInteractor.postSelectGroupId(it[position].id)
                finish()
            }
        }
    }
}