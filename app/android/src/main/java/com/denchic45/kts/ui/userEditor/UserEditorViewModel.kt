package com.denchic45.kts.ui.userEditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.R
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.*
import com.denchic45.kts.ui.AndroidUiComponent
import com.denchic45.kts.ui.AndroidUiComponentDelegate
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.ui.usereditor.UserEditorUILogicDelegate
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class UserEditorViewModel @Inject constructor(
    @Named("UserEditor ${UserEditorFragment.USER_ID}")
    private val _userId: String?,
    observeUserUseCase: ObserveUserUseCase,
    addUserUseCase: AddUserUseCase,
    private val removeUserUseCase: RemoveUserUseCase,
    private val confirmInteractor: ConfirmInteractor,
    private val componentContext: ComponentContext,
) : UserEditorUILogicDelegate(
    observeUserUseCase,
    addUserUseCase,
    _userId?.toUUID(),
    componentContext
), AndroidUiComponent by AndroidUiComponentDelegate(componentContext) {

    override fun onOptionClick(itemId: Int) {
        when (itemId) {
            R.id.option_user_save -> onSaveClick()
            R.id.option_user_delete -> onDeleteUser()
        }
    }

    fun onBackPress() {
        if (uiEditor.hasBeenChanged()) {
            if (uiEditor.isNew) confirmExit(
                Pair(
                    "Отменить создание?",
                    "Новый пользователь не будет сохранен"
                )
            ) else confirmExit(
                Pair("Отменить редактирование?", "Внесенные изменения не будут сохранены")
            )
        } else {
            componentScope.launch { finish() }
        }
    }


    override fun onFinish() {
        componentScope.launch { finish() }
    }


    override fun onDeleteUser() {
        openConfirmation(
            Pair(
                "Удаление пользователя",
                "Удаленного пользователя нельзя будет восстановить"
            )
        )

        componentScope.launch {
            if (confirmInteractor.receiveConfirm()) {
                removeUserUseCase(userId!!)
                    .onSuccess { finish() }
                    .onFailure { showToast("Произошла ошибка") }
            }
        }
    }

    private fun confirmExit(titleWithSubtitlePair: Pair<String, String>) {
        componentScope.launch {
            openConfirmation(titleWithSubtitlePair)
            if (confirmInteractor.receiveConfirm()) {
                finish()
            }
        }
    }


    override fun onCreateOptions() {
        super.onCreateOptions()
        if (uiEditor.isNew) {
            componentScope.launch {
                setMenuItemVisible(R.id.option_user_delete to false)
            }
        }
    }
}