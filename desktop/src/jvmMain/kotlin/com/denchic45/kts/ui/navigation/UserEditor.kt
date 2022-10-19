package com.denchic45.kts.ui.navigation

import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.ui.usereditor.UserEditorComponent

class UserEditorConfig(
    val userId: String?,
    val role: UserRole,
    val groupId: String?,
) : GroupMembersConfig

class UserEditorChild(val userEditorComponent: UserEditorComponent) : GroupMembersChild