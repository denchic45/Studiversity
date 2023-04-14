package com.denchic45.kts.ui

sealed class UiColor {

    data class ColorName(val value: String) : UiColor()

    data class ColorId(val value: Int) : UiColor()
}

fun UiColor.onName(fn: (success: String) -> Unit): UiColor =
    this.apply { if (this is UiColor.ColorName) fn(value) }

fun UiColor.onVector(fn: (failure: Int) -> Unit): UiColor =
    this.apply { if (this is UiColor.ColorId) fn(value) }