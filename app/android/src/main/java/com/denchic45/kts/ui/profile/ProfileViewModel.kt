package com.denchic45.kts.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.ObserveUserUseCase
import com.denchic45.kts.ui.AndroidUiComponent
import com.denchic45.kts.ui.AndroidUiComponentDelegate
import com.denchic45.stuiversity.util.toUUID
import javax.inject.Inject
import javax.inject.Named

class ProfileViewModel @Inject constructor(
    @Named(ProfileFragment.USER_ID) userId: String,
    observeUserUseCase: ObserveUserUseCase,
    private val componentContext: ComponentContext
) : ProfileUiLogic(observeUserUseCase, userId.toUUID(), componentContext),
    AndroidUiComponent by AndroidUiComponentDelegate(componentContext) {

    val openFullImage = SingleLiveData<String>()
    val openGallery = SingleLiveData<Void>()

    override fun onAvatarClick() {
        profileViewState.value.onSuccess {
            openFullImage.value = it.avatarUrl
        }
    }

    override fun onOptionClick(itemId: Int) {
        when (itemId) {
            R.id.menu_select_avatar -> openGallery.call()
        }
    }

//    fun onImageLoad(imageBytes: ByteArray?) {
//        viewModelScope.launch {
//            interactor.updateAvatar(userOfProfile!!, imageBytes!!)
//        }
//    }

    companion object {
        private const val PERMISSION_USER_NFO = "PERMISSION_USER_NFO"
    }
}