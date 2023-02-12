package com.denchic45.kts.util

import java.util.*

object UUIDS {
    fun createShort(): String = UUID.randomUUID().toString().substring(0, 13)
}

fun randomAlphaNumericString(desiredStrLength: Int): String {
    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..desiredStrLength)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}