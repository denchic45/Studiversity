package com.denchic45.studiversity.ui.model

import com.denchic45.studiversity.ui.UiColor
import com.denchic45.studiversity.ui.UiImage
import com.denchic45.studiversity.ui.UiText

data class OptionItem(
    val id: String,
    val title: UiText,
    val icon: UiImage? = null,
    val color: UiColor? = null,
    val enable: Boolean = true,
) {

    fun hasIcon(): Boolean = icon != null
}