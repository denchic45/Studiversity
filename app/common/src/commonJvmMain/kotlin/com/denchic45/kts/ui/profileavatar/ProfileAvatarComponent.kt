package com.denchic45.kts.ui.profileavatar

import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class ProfileAvatarComponent(
    @Assisted
    private val avatarUrl:UUID,
    @Assisted
    private val isOwned: Boolean,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {


}