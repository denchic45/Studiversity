package com.denchic45.studiversity.uivalidator.validatorlistener

import com.denchic45.studiversity.uivalidator.Rule
import java.util.function.Consumer

class ActionValidationListener(
    private val errorAction: Consumer<Rule>,
    private val successAction: Runnable
) : ValidationListener() {
    override fun onSuccess() {
        successAction.run()
    }

    override fun onError(rule: Rule) {
        errorAction.accept(rule)
    }
}