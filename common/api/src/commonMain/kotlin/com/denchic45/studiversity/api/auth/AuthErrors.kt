package com.denchic45.studiversity.api.auth

object AuthErrors {
    const val REFRESH_TOKEN_REQUIRED = "REFRESH_TOKEN_REQUIRED"
    const val USER_ALREADY_REGISTERED = "USER_ALREADY_REGISTERED"

    const val INVALID_GRANT = "INVALID_GRANT"
    const val INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN"
}