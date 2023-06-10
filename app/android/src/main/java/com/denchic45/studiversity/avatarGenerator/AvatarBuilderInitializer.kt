package com.denchic45.studiversity.avatarGenerator

interface AvatarBuilderInitializer {
    fun initBuilder(builder: AvatarGenerator.Builder): AvatarGenerator.Builder
}