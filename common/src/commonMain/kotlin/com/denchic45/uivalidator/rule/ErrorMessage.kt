package com.denchic45.uivalidator.rule

sealed class ErrorMessage {

    data class ResourceMessage(val messageRes: Int) : ErrorMessage()

    data class StringMessage(val message: String) : ErrorMessage()

    val isString get() = this is StringMessage

    val isId get() = this is ResourceMessage

//    fun fold(fnL: (Int) -> Any, fnR: (String) -> Any): Any =
//        when (this) {
//            is ResourceMessage -> fnL(messageRes)
//            is StringMessage -> fnR(message)
//        }
}

fun ErrorMessage.onString(fn: (value: String) -> Unit): ErrorMessage =
    this.apply { if (this is ErrorMessage.StringMessage) fn(message) }

fun ErrorMessage.onResource(fn: (failure: Int) -> Unit): ErrorMessage =
    this.apply { if (this is ErrorMessage.ResourceMessage) fn(messageRes) }