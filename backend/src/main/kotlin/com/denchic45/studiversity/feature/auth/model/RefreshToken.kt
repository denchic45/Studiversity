package com.denchic45.studiversity.feature.auth.model

import java.time.Instant
import java.util.*

data class RefreshToken(
    val userId: UUID,
    val token: String,
    val expireAt: Instant
) {
    val isExpired: Boolean
        get() = expireAt < Instant.now()
}
