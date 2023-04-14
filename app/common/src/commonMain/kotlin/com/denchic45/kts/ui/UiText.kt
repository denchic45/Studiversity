package com.denchic45.kts.ui


sealed class UiText {

    data class StringText(val value: String) : UiText()

    data class IdText(val value: Int) : UiText()

    data class FormattedText(val value: Int, val formatArgs: Any?) : UiText()

    data class FormattedQuantityText(
        val value: Int,
        val quantity: Int,
        val formatArgs: Any?
    ) : UiText()

    fun fold(fnL: (Int) -> Any, fnR: (String) -> Any): Any = when (this) {
        is IdText -> fnL(value)
        is StringText -> fnR(value)
        else -> throw IllegalStateException()
    }
}

fun UiText.onString(fn: (success: String) -> Unit): UiText =
    this.apply { if (this is UiText.StringText) fn(value) }

fun UiText.onVector(fn: (failure: Int) -> Unit): UiText =
    this.apply { if (this is UiText.IdText) fn(value) }

fun uiTextOf(value: String) = UiText.StringText(value)

fun uiTextOf(value:Int) = UiText.IdText(value)