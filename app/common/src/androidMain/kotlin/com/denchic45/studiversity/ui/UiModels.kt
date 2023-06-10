package com.denchic45.studiversity.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource

fun UiText.get(context: Context): String = when(this) {
    is UiText.StringText -> value
    is UiText.ResourceText -> context.getString(value)
    is UiText.FormattedText -> context.resources.getString(value, formatArgs)
    is UiText.FormattedQuantityText -> context.resources.getQuantityString(value, quantity, formatArgs)
}

@Composable
fun UiIcon.getPainter():Painter = when(this) {
    is UiIcon.Resource -> painterResource(id = value)
    is UiIcon.Vector -> rememberVectorPainter(image = value)
}