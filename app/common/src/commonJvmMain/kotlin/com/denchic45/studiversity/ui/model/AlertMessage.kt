package com.denchic45.studiversity.ui.model

import com.denchic45.studiversity.domain.resource.Failure

sealed class AlertMessage {
    data class Info(val title: String, val message: String) : AlertMessage()
    data class Loading(val loading: String) : AlertMessage()
    data class Error(val message: String) : AlertMessage()
    data class Success(val message: String) : AlertMessage()
    data class Unknown(val failure: Failure) : AlertMessage()
}