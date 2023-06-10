package com.denchic45.studiversity

import androidx.core.content.res.ResourcesCompat
import com.denchic45.studiversity.avatarGenerator.AvatarBuilderInitializer
import com.denchic45.studiversity.avatarGenerator.AvatarGenerator

class AvatarBuilderTemplate : AvatarBuilderInitializer {
    override fun initBuilder(builder: AvatarGenerator.Builder): AvatarGenerator.Builder {
        return builder
            .font(ResourcesCompat.getFont(builder.context, R.font.futura_medium))
    }
}