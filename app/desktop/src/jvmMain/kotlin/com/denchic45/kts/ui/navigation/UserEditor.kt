package com.denchic45.kts.ui.navigation

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import java.util.UUID

class UserEditorConfig(
    val userId: UUID?,
    val role: UserRole,
    val groupId: UUID?,
) : GroupMembersConfig, OverlayConfig

class UserEditorChild(
    val userEditorComponent: UserEditorComponent
) : GroupMembersChild, OverlayChild