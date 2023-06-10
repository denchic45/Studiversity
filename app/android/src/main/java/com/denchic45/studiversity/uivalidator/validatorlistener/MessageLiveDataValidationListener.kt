package com.denchic45.studiversity.uivalidator.validatorlistener

import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.uivalidator.Rule

class MessageLiveDataValidationListener(mutableLiveData: SingleLiveData<String?>) :
    LiveDataValidationListener<String?>(mutableLiveData) {
    override fun onSuccess() {
        mutableLiveData.value = null
    }

    override fun onError(rule: Rule) {
        mutableLiveData.value = rule.errorMessage
    }
}