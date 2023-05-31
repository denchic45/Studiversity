package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class UsersAdminComponent(
    @Assisted
    componentContext: ComponentContext
) :ComponentContext by componentContext {
}