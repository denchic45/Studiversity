package com.denchic45.kts.ui.navigation

import com.denchic45.kts.ui.profile.ProfileComponent
import java.util.UUID

class ProfileConfig(val userId: UUID) : GroupMembersConfig

class ProfileChild(val profileComponent: ProfileComponent) : GroupMembersChild