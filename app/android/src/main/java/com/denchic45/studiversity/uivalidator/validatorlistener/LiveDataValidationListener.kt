package com.denchic45.studiversity.uivalidator.validatorlistener

import com.denchic45.studiversity.SingleLiveData

abstract class LiveDataValidationListener<T : Any?>(
    protected val mutableLiveData: SingleLiveData<T>
) : ValidationListener()