package com.denchic45.studiversity.uivalidator.experimental

import com.denchic45.studiversity.uivalidator.rule.ErrorMessage

interface Validatable {

//    val onSuccess: (() -> Unit)?
//    val onError: ((ErrorMessage) -> Unit)?



    fun validate(): Boolean
}