package com.denchic45.studiversity.feature.user.account.model

import java.util.*

data class VerificationToken(
    val secret: String,
    val userId: UUID,
    val action: TokenAction,
    val expired: Boolean
)
