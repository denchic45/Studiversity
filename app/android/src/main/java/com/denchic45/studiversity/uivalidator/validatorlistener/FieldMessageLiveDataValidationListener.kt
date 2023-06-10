package com.denchic45.studiversity.uivalidator.validatorlistener

import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.uivalidator.Rule

class FieldMessageLiveDataValidationListener(
    private val id: Int,
    mutableLiveData: SingleLiveData<Pair<Int, String?>>
) : LiveDataValidationListener<Pair<Int, String?>>(mutableLiveData) {
    override fun onSuccess() {
        mutableLiveData.value = Pair<Int, String?>(id, null)
    }

    override fun onError(rule: Rule) {
        mutableLiveData.value = Pair(id, rule.errorMessage)
    }
}