package com.denchic45.studiversity.ui.profile

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.R
import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.usecase.ObserveUserUseCase
import com.denchic45.studiversity.ui.AndroidUiComponent
import com.denchic45.studiversity.ui.AndroidUiComponentDelegate
import com.denchic45.stuiversity.util.toUUID
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

//@Inject
//class ProfileViewModel constructor(
//    observeUserUseCase: ObserveUserUseCase,
//    @Assisted
//    _userId: String,
//    @Assisted
//    private val componentContext: ComponentContext
//) : ProfileUiLogic(observeUserUseCase, _userId.toUUID(), componentContext),
//    AndroidUiComponent by AndroidUiComponentDelegate(componentContext) {
//
//    val openFullImage = SingleLiveData<String>()
//    val openGallery = SingleLiveData<Void>()
//
//    override fun onAvatarClick() {
//        profileViewState.value.onSuccess {
//            openFullImage.value = it.avatarUrl
//        }
//    }
//
//    override fun onOptionClick(itemId: Int) {
//        when (itemId) {
//            R.id.menu_select_avatar -> openGallery.call()
//        }
//    }
//
////    fun onImageLoad(imageBytes: ByteArray?) {
////        viewModelScope.launch {
////            interactor.updateAvatar(userOfProfile!!, imageBytes!!)
////        }
////    }
//
//    companion object {
//        private const val PERMISSION_USER_NFO = "PERMISSION_USER_NFO"
//    }
//}