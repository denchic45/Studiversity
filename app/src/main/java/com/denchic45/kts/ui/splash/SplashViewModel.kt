package com.denchic45.kts.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denchic45.kts.data.service.AuthService
import javax.inject.Inject

class SplashViewModel @Inject constructor(authService: AuthService) : ViewModel() {

    var nextScreenMLiveData = MutableLiveData<NextActivity>()

    enum class NextActivity { MAIN, LOGIN }

    init {
        if (authService.isAuthenticated) {
            nextScreenMLiveData.setValue(NextActivity.MAIN)
        } else {
            nextScreenMLiveData.setValue(NextActivity.LOGIN)
        }
    }
}