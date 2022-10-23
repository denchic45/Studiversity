package com.denchic45.uivalidator.experimental2

import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.condition.observable
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import com.denchic45.uivalidator.experimental2.validator.observable
import org.junit.Test

internal class ValidationTest {

    private val testPhoneNum = "+79510832144"
    private val testPhoneNum2 = "+7951750383"
    private val testPhoneNum3 = "+7951002348"

    private val testEmail = "User@gmail.com"
    private val testEmail2 = "AnotherUser@gmail.com"
    private val testEmail3 = "Test@gmail.com"

    @Test
    fun testInputEmailOrPhoneNum() {
        var field = ""
        val phoneNumValidator = ValueValidator(
            value = { field },
            conditions = listOf(
                Condition<String> { it.isPhoneNum() }
                    .observable { if (it) println("Введен номер") },
                Condition<String> { it.isPhoneNumExist() }
                    .observable { if (!it) println("Такого номера не существует") }
            ),
            operator = Operator.all()
        )

        val emailValidator = ValueValidator(
            value = { field },
            conditions = listOf(
                Condition<String> { it.isEmail() }
                    .observable { if (it) println("Введена почта") },
                Condition<String> { field.isEmailExist() }
                    .observable { if (!it) println("Такой почты не существует") }
            ),
            operator = Operator.all()
        )

        val validator = CompositeValidator(
            validators = listOf(phoneNumValidator, emailValidator),
            operator = Operator.anyEach()
        ).observable { println(if (it) "phone num or email is correct" else "nothing correct") }

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