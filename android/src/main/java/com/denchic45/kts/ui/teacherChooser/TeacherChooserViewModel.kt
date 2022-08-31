package com.denchic45.kts.ui.teacherChooser

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.data.Resource
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.model.toUserItem
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.usecase.FindTeacherByContainsNameUseCase
import com.denchic45.kts.ui.base.chooser.ChooserViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeacherChooserViewModel @Inject constructor(
    private val teacherChooserInteractor: TeacherChooserInteractor,
    findTeacherByContainsNameUseCase: FindTeacherByContainsNameUseCase,
) : ChooserViewModel<UserItem>() {

    private var _users: List<User> = emptyList()

    override val sourceFlow = { name: String ->
        findTeacherByContainsNameUseCase.invoke(name)
            .map {
                when (it) {
                    is Resource.Error -> Resource.Error(it.error)
                    Resource.Loading -> Resource.Loading
                    is Resource.Success -> {
                        _users = it.data
                        Resource.Success(it.data.map(User::toUserItem))
                    }
                    is Resource.Next -> throw IllegalStateException()
                }
            }
    }

    override fun onItemSelect(item: UserItem) {
        viewModelScope.launch {
            teacherChooserInteractor.postSelectedCurator(_users.first { item.id == item.id })
            finish()
        }
    }

}