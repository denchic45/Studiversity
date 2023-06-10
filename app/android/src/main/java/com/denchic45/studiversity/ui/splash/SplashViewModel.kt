package com.denchic45.studiversity.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denchic45.studiversity.data.service.AuthService
import javax.inject.Inject

class SplashViewModel : ViewModel() {

    var nextScreenMLiveData = MutableLiveData<NextActivity>()

    enum class NextActivity { MAIN, LOGIN }

    init {
//        if (authService.isAuthenticated) {
//            nextScreenMLiveData.setValue(NextActivity.MAIN)
//        } else {
//            nextScreenMLiveData.setValue(NextActivity.LOGIN)
//        }
    }
}