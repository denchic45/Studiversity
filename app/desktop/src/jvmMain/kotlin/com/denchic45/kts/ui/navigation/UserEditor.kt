package com.denchic45.kts.ui.navigation

import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.stuiversity.api.role.model.Role
import java.util.UUID

class UserEditorConfig(
    val userId: UUID?,
    val role: Role,
) : GroupMembersConfig, OverlayConfig

class UserEditorChild(
    val userEditorComponent: UserEditorComponent,
    val appBarInteractor: AppBarInteractor
) : GroupMembersChild, OverlayChild