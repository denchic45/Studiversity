package com.denchic45.kts.ui

import android.content.Context

fun UiText.get(context: Context): String = when(this) {
    is UiText.StringText -> value
    is UiText.IdText -> context.getString(value)
    is UiText.FormattedText -> context.resources.getString(value, formatArgs)
    is UiText.FormattedQuantityText -> context.resources.getQuantityString(value, quantity, formatArgs)

}