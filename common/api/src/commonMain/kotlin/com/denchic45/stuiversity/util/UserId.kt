package com.denchic45.stuiversity.util

import java.util.UUID

sealed interface UserId {

    val value: String

    data class Id(val id: UUID) : UserId {
        override val value: String
            get() = id.toString()
    }

    data object Me : UserId {
        override val value = "me"
    }
}

fun userIdOf(id: UUID) = UserId.Id(id)

fun userIdOfMe() = UserId.Me

fun userIdOrMe(id: UUID?) = id?.let(::userIdOf) ?: userIdOfMe()

val UUID?.orMe: String
    get() = this?.toString() ?: "me"