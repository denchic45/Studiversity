package com.denchic45.kts.ui.validationtest

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.util.componentScope
import com.denchic45.uivalidator.experimental.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ValidationTestComponent(componentContext: ComponentContext) :
    ComponentContext by componentContext {

    val testPhoneNum = "+79510832144"
    val testPhoneNum2 = "+7951750383"
    val testPhoneNum3 = "+7951002348"

    val testEmail = "User@gmail.com"
    val testEmail2 = "AnotherUser@gmail.com"
    val testEmail3 = "Test@gmail.com"

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

    val field = MutableStateFlow("")

    val fieldError = MutableStateFlow<String?>(null)

    val result = MutableStateFlow<String?>(null)

    val validateEnabled = MutableStateFlow(false)

    private val coroutineScope = componentScope()

    private val phoneNumValidation = Validator(
        listOf(Condition(
            value = { field.value },
            predicate = { it.isPhoneNum() }
        ),
            Condition(
                value = { field.value },
                predicate = { it.isPhoneNumExist() }
            ) { if (!it) fieldError.value = "Такого номера не существует" }
        ),
        Operator.All
    )
    private val emailValidation = Validator(
        listOf(Condition(
            value = { field.value },
            predicate = { it.isEmail() }
        ),
            Condition(
                value = { field.value },
                predicate = { it.isEmailExist() }
            ) { if (!it) fieldError.value = "Такой почты не существует" }
        ),
        Operator.All
    )
    private val emailOrPhoneValidator = Validator(conditions = listOf(
        phoneNumValidation,
        emailValidation
    ), operator = Operator.Any)
    private val validator = Validator(conditions = listOf(
        Condition(
            value = { field.value },
            predicate = { it.isNotEmpty() },
        ) {
            println("On result empty:$it")
            fieldError.value = if (!it) "Заполните поле!" else null
            validateEnabled.value = it
        }.triggerOn(field, coroutineScope),
        Condition(
            value = { field.value },
            predicate = { it.isEmail() or it.isPhoneNum() }) {
            if (!it) fieldError.value = "Ни почта ни тел не правильны"
        },
        emailOrPhoneValidator,
    )) {
        coroutineScope.launch {
            println("Final validation: $it")
            result.emit(if (it) "Валидация прошла успешно!" else "Валидация не пройдена")
        }
    }

    fun onTextChange(text: String) {
        println("text change: $text")
        field.value = text
    }

    fun onValidateClick() {
        validator.validate()
    }
}