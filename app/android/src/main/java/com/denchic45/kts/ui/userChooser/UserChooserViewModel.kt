package com.denchic45.kts.ui.userChooser

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.FindUserByContainsNameUseCase
import com.denchic45.kts.domain.usecase.UserChooserInteractor
import com.denchic45.kts.ui.base.chooser.ChooserViewModel
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserChooserViewModel @Inject constructor(
    private val userChooserInteractor: UserChooserInteractor,
    private val findUserByContainsNameUseCase: FindUserByContainsNameUseCase
) : ChooserViewModel<UserResponse>() {

    override val sourceFlow: (String) -> Flow<Resource<List<UserResponse>>> = {
        flow { emit(findUserByContainsNameUseCase(it)) }
    }

    override fun onItemClick(position: Int) {
        items.value.onSuccess {
            viewModelScope.launch {
                userChooserInteractor.post(it[position].id)
                finish()
            }
        }
    }
}