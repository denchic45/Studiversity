package com.denchic45.uivalidator.experimental

import kotlin.test.Test


internal class ValidatorTest {

    private val testPhoneNum = "+79510832144"
    private val testPhoneNum2 = "+7951750383"
    private val testPhoneNum3 = "+7951002348"

    private val testEmail = "User@gmail.com"
    private val testEmail2 = "AnotherUser@gmail.com"
    private val testEmail3 = "Test@gmail.com"

    @Test
    fun testInputEmailOrPhoneNum() {

        var field = ""

        val phoneNumValidator = Validator(
            conditions = listOf(
                Condition(
                    value = { field },
                    predicate = { it.isPhoneNum() }
                ) { if (it) println("Введен номер") },
                Condition(
                    value = { field },
                    predicate = { field.isPhoneNumExist() }
                ) { if (!it) println("Такого номера не существует") }
            ),
            operator = Operator.All
        )

        val emailValidator = Validator(
            conditions = listOf(
                Condition(
                    value = { field },
                    predicate = { it.isEmail() }
                ) { if (it) println("Введена почта") },
                Condition(
                    value = { field },
                    predicate = { it.isEmailExist() }
                ) { if (!it) println("Такой почты не существует") }
            ),
            operator = Operator.All
        )

        val validator = Validator(
            conditions = listOf(phoneNumValidator, emailValidator),
            operator = Operator.AnyEach
        ) {
            println(if (it) "phone num or email is correct" else "nothing correct")
        }

        field = testPhoneNum2

        validator.validate()

    }

    private fun String.isPhoneNum(): Boolean {
        return startsWith("+")
    }

    private fun String.isEmail(): Boolean {
        return contains("@")
    }

    private fun String.isPhoneNumExist(): Boolean {
        return setOf(testPhoneNum, testPhoneNum2).contains(this)
    }

    private fun String.isEmailExist(): Boolean {
        return setOf(testEmail, testEmail2).contains(this)
    }
}