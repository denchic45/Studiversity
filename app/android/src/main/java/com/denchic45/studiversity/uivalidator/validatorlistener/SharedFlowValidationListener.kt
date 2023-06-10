package com.denchic45.studiversity.uivalidator.validatorlistener

import kotlinx.coroutines.flow.MutableSharedFlow

abstract class SharedFlowValidationListener<T>(protected val mutableSharedFlow: MutableSharedFlow<T>) :
    ValidationListener()