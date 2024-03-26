package com.denchic45.studiversity.feature.user.account.model

import java.util.*

data class VerificationToken(
    val secret: UUID,
    val userId: UUID,
    val payload: String,
    val expired: Boolean
)
