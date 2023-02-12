package com.studiversity.feature.auth

import kotlin.random.Random

class PasswordGenerator {
    private companion object {
        val upperAlphabets: Array<Char> = arrayOf(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
        )
        val lowerAlphabets: Array<Char> = arrayOf(
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
        )
        val symbols: Array<Char> = arrayOf('!', '@', '#', '$', '%', '&', '?')
        val digits: Array<Char> = arrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    }

    fun generate(length: Int = 8): String = buildString(length) {
        repeat(length) {
            when (Random.nextInt(1, 5)) {
                1 -> append(upperAlphabets[Random.nextInt(upperAlphabets.size)])
                2 -> append(lowerAlphabets[Random.nextInt(lowerAlphabets.size)])
                3 -> append(symbols[Random.nextInt(symbols.size)])
                4 -> append(digits[Random.nextInt(digits.size)])
            }
        }
    }
}