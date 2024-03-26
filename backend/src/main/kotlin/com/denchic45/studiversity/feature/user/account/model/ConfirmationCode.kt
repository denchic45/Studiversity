package com.denchic45.studiversity.feature.user.account.model

import java.util.*

data class ConfirmationCode(val code: String, val userId: UUID, val expired: Boolean)