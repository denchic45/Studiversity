package com.denchic45.uivalidator.experimental

import kotlin.test.Test


internal class ValidatorTest {

    val testPhoneNum = "+79510832144"
    val testEmail = "User@gmail.com"

    @Test
    fun testInputEmailOrPhoneNum() {

        var field = ""


        val validator = Validator(
            conditions = listOf(
                Condition(
                    value = { field },
                    predicate = { it.isPhoneNum() }) {
                    println(if (!it) "Is not phone num!" else "phone num is correct")
                },
                Condition(
                    value = { field },
                    predicate = { it.isEmail() }) {
                    println(if (!it) "Is not email!" else "email is correct")
                }
            ),
            operator = Operator.AnyEach
        ) {
            println(if (it) "phone num or email is correct" else "nothing correct")
        }


        field = testPhoneNum

        validator.validate()

    }

    private fun String.isPhoneNum(): Boolean {
        return startsWith("+")
    }

    private fun String.isEmail(): Boolean {
        return contains("@")
    }
}