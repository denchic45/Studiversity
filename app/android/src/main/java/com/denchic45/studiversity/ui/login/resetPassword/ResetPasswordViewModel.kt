package com.denchic45.studiversity.ui.login.resetPassword

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.ui.base.BaseViewModel
import com.denchic45.studiversity.util.Validations
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResetPasswordViewModel @Inject constructor(
//    private val authService: AuthService,
) : BaseViewModel() {

    val showErrorFieldEmail = SingleLiveData<Boolean>()
    fun onSendClick(email: String) {
        if (Validations.notValidEmail(email)) {
            showErrorFieldEmail.value = true
            return
        }
        showErrorFieldEmail.value = false
        viewModelScope.launch {
            try {
//                authService.resetPassword(email)
                showSnackBar("Письмо успешно отправлено на почту")
                finish()
            } catch (t: Throwable) {
                showErrorFieldEmail.value = true
                showSnackBar("Произошла ошибка! Возможно неверная почта")
            }
        }
    }
}