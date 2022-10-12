package com.denchic45.uivalidator.experimental

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.test.Test


internal class ValidatorTest {

    @Test
    fun test() {
        println("Starting test...")
        val condition1 = Condition(
            value = { "Ivan@gmail.com" },
            predicate = { it.contains("Ivan") },
            onResult = { isValid ->
                println(
                    if (isValid) "#value is correct"
                    else "#value no contains: Ivan"
                )
            }
        )

        val condition11 = Condition(
            value = { "123" },
            predicate = { it == "123" },
            onResult = { isValid ->
                println(
                    if (isValid) "#value is equal"
                    else "#value not equal"
                )
            }
        )

        val condition2 = Condition({ "Ivan" }, { it.startsWith("1") }) { isValid ->
            if (!isValid) println("#value not starts with 1")
        }

        val conditionSource = MutableStateFlow("Text not starts with 1")

        val errorFlow = MutableStateFlow<String?>("")

        GlobalScope.launch {
            println()
            errorFlow.collect { println(it) }
        }

        val combinedCondition =
            CombineCondition(conditions = listOf(condition1, condition2)) { isValid ->
                if (isValid) {
                    println("All combined conditions valid")
                } else {
                    println("Has not valid combined conditions")
                }
            }

        val condition3 = Condition(
            { conditionSource.value },
            { it.startsWith("1") },
            stateFlowResult(errorFlow) { conditionSource.value + " не начинается на единицу" }
        )


        val validator = Validator(condition1, condition11, condition3, combinedCondition) {
            println("Validator is ${if (it) "valid" else "not valid"}!")
        }
        validator.validate()
    }
}