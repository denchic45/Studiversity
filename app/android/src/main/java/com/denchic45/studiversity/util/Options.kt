package com.denchic45.studiversity.util

import com.denchic45.studiversity.R
import com.denchic45.studiversity.ui.model.OptionItem
import com.denchic45.studiversity.ui.UiText

class Options {
    companion object {
        fun student() = listOf(
            OptionItem(
                id = "OPTION_SET_HEADMAN",
                title = UiText.ResourceText(R.string.option_set_headman)
            ),
            OptionItem(
                id = "OPTION_EDIT_USER",
                title = UiText.ResourceText(R.string.option_edit_user)
            ),
            OptionItem(
                id = "OPTION_REMOVE_USER",
                title = UiText.ResourceText(R.string.option_remove_user)
            ),
        )
    }
}