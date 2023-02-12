package com.denchic45.kts.ui.teacherChooser

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.domain.error.SearchError
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.usecase.FindTeacherByContainsNameUseCase
import com.denchic45.kts.ui.base.chooser.ChooserViewModel
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.model.toUserItem
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class TeacherChooserViewModel @Inject constructor(
    private val teacherChooserInteractor: TeacherChooserInteractor,
    findTeacherByContainsNameUseCase: FindTeacherByContainsNameUseCase,
) : ChooserViewModel<UserItem>() {

    private var _users: List<User> = emptyList()

    override val sourceFlow: (String) -> Flow<Result<List<UserItem>, SearchError>> =
        { name: String ->
            findTeacherByContainsNameUseCase.invoke(name)
                .map { result -> result.map { users -> users.map(User::toUserItem) } }
        }

    override fun onItemSelect(item: UserItem) {
        viewModelScope.launch {
            teacherChooserInteractor.postSelectedCurator(_users.first { user -> user.id == item.id })
            finish()
        }
    }

}