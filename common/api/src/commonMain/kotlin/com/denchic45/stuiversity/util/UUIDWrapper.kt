package com.denchic45.stuiversity.util

import java.util.*

sealed interface UUIDWrapper {

    val value: String

    data class UUID(val uuid: java.util.UUID) : UUIDWrapper {
        override val value: String
            get() = uuid.toString()
    }

    object Me : UUIDWrapper {
        override val value = "me"
    }
}

fun uuidOf(uuid: UUID) = UUIDWrapper.UUID(uuid)

fun uuidOfMe() = UUIDWrapper.Me