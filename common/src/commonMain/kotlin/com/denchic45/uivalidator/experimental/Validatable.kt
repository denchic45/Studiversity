package com.denchic45.uivalidator.experimental

import com.denchic45.uivalidator.rule.ErrorMessage

interface Validatable {

//    val onSuccess: (() -> Unit)?
//    val onError: ((ErrorMessage) -> Unit)?



    fun validate(): Boolean
}