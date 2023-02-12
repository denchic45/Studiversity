package com.studiversity.feature.auth.model

import java.time.Instant
import java.util.*

data class MagicLinkToken(
    val userId: UUID,
    val token: String,
    val expireAt: Instant
) {
    val isExpired: Boolean
        get() = expireAt < Instant.now()
}