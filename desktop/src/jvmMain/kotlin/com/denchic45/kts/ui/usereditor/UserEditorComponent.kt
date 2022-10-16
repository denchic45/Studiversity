package com.denchic45.kts.ui.usereditor

import com.denchic45.kts.ui.model.MenuAction
import com.denchic45.kts.ui.model.MenuItem

class UserEditorComponent {
    val genders = listOf(MenuItem(GenderAction.Male), MenuItem(GenderAction.Female))

    fun onGenderSelect(action: GenderAction) {
        when (action) {
            GenderAction.Male -> TODO()
            GenderAction.Female -> TODO()
        }
    }
}

enum class GenderAction(override val title: String, override val iconName: String? = null) :
    MenuAction {
    Male("Мужской"),
    Female("Женский")
}