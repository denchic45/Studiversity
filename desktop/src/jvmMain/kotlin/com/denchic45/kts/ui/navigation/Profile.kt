package com.denchic45.kts.ui.navigation

import com.denchic45.kts.ui.profile.ProfileComponent

class ProfileConfig(val userId: String) : GroupMembersConfig

class ProfileChild(val profileComponent: ProfileComponent) : GroupMembersChild