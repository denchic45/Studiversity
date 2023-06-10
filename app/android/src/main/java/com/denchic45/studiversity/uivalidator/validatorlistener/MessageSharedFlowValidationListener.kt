package com.denchic45.studiversity.uivalidator.validatorlistener

import com.denchic45.studiversity.uivalidator.Rule
import kotlinx.coroutines.flow.MutableSharedFlow

class MessageSharedFlowValidationListener(sharedFlow: MutableSharedFlow<String>) :
    SharedFlowValidationListener<String>(sharedFlow) {
    override fun onSuccess() {
    }

    override fun onError(rule: Rule) {
        mutableSharedFlow.tryEmit(rule.errorMessage ?: "")
    }
}