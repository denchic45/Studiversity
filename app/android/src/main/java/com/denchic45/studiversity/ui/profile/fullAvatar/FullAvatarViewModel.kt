package com.denchic45.studiversity.ui.profile.fullAvatar

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.R
import com.denchic45.studiversity.ui.avatar.FullImageActivity
import com.denchic45.studiversity.ui.base.BaseViewModel
import com.denchic45.studiversity.util.NetworkException
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class FullAvatarViewModel @Inject constructor(
    @Named(FullImageActivity.IMAGE_URL)
    private val photoUrl: String,
    private val interactor: FullAvatarInteractor
) : BaseViewModel() {

    private var thisUser: UserResponse? = null
    override fun onCreateOptions() {
        super.onCreateOptions()
        thisUser = interactor.findThisUser()
        viewModelScope.launch {
            if (photoUrl != thisUser!!.avatarUrl) {
                setMenuItemVisible(R.id.menu_delete_avatar to false)
            }
        }
    }

    override fun onOptionClick(itemId: Int) {
        when (itemId) {
            android.R.id.home -> viewModelScope.launch { finish() }
            R.id.menu_delete_avatar -> {
                viewModelScope.launch {
                    try {
                        interactor.removeUserAvatar(thisUser!!)
                        finish()
                    } catch (e: Exception) {
                        if (e is NetworkException) {
                            showToast(R.string.error_check_network)
                        } else {
                            showSnackBar("Произошла ошибка")
                        }
                    }
                }
            }
        }
    }

}