package com.denchic45.studiversity.ui


sealed class UiText {

    data class StringText(val value: String) : UiText()

    data class ResourceText(val value: Int) : UiText()

    data class FormattedText(val value: Int, val formatArgs: Any?) : UiText()

    data class FormattedQuantityText(
        val value: Int,
        val quantity: Int,
        val formatArgs: Any?
    ) : UiText()

    fun fold(fnL: (Int) -> Any, fnR: (String) -> Any): Any = when (this) {
        is ResourceText -> fnL(value)
        is StringText -> fnR(value)
        else -> throw IllegalStateException()
    }
}

inline fun UiText.onString(fn: (success: String) -> Unit): UiText =
    this.apply { if (this is UiText.StringText) fn(value) }

inline fun UiText.onResource(fn: (failure: Int) -> Unit): UiText =
    this.apply { if (this is UiText.ResourceText) fn(value) }

fun uiTextOf(value: String) = UiText.StringText(value)

fun uiTextOf(value:Int) = UiText.ResourceText(value)

fun UiText.asString() = (this as UiText.StringText).value