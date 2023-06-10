package com.denchic45.studiversity.uivalidator.validatorlistener

import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.uivalidator.Rule

class MessageIdLiveDataValidationListener(mutableLiveData: SingleLiveData<Int>) :
    LiveDataValidationListener<Int>(mutableLiveData) {
    override fun onSuccess() {
//        mutableLiveData.value = null
    }

    override fun onError(rule: Rule) {
        mutableLiveData.value = rule.errorResId
    }
}