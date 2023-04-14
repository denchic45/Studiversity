package com.denchic45.kts.ui

import androidx.compose.ui.graphics.vector.ImageVector


sealed class UiIcon {

    data class Resource(val value: Int) : UiIcon()

    data class Vector(val value: ImageVector) : UiIcon()
}

inline fun UiIcon.onResource(fn: (value: Int) -> Unit): UiIcon =
    this.apply { if (this is UiIcon.Resource) fn(value) }

inline fun UiIcon.onVector(fn: (value: ImageVector) -> Unit): UiIcon =
    this.apply { if (this is UiIcon.Vector) fn(value) }

fun uiIconOf(value:Int) = UiIcon.Resource(value)

fun uiIconOf(value: ImageVector) = UiIcon.Vector(value)