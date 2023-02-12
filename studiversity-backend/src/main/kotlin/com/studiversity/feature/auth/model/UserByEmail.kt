package com.studiversity.feature.auth.model

import java.util.UUID

data class UserByEmail(
    val id:UUID,
    val email:String,
    val password:String
)
