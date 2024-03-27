package com.denchic45.studiversity.util

fun String.containsUpperAndLowerCaseChars() = contains("(?=.*[a-z])(?=.*[A-Z])".toRegex())

fun String.containsLatinChars() = contains("[a-zA-Z]".toRegex())

fun String.containsDigits() = contains("[0-9]".toRegex())
