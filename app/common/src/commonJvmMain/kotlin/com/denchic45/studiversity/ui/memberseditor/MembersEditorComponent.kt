package com.denchic45.studiversity.ui.memberseditor

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class MembersEditorComponent(
    @Assisted
    componentContext: ComponentContext):ComponentContext by componentContext {
}