//package com.denchic45.uivalidator.experimental
//
//import com.denchic45.uivalidator.rule.ErrorMessage
//
//class CombineCondition<T>(
//    private val conditions: List<ICondition<T>>,
////     val value: () -> T,
////     val errorMessage: (value: T) -> ErrorMessage = { getErrorMessage() },
////     val onError: (ErrorMessage) -> Unit = EmptyOnError
//) : ICondition<T> {
//
//    val currentCodition
//
//    override val value: () -> T
//        get() = TODO("Not yet implemented")
//    override val errorMessage: (value: T) -> ErrorMessage
//        get() = TODO("Not yet implemented")
//    override val onError: (errorMessage: ErrorMessage) -> Unit
//        get() = TODO("Not yet implemented")
//
//    companion object {
//        val EmptyOnError: (ErrorMessage) -> Unit = {}
//    }
//
//    override fun isValid(): Boolean {
//        return conditions.all { it.isValid() }
//    }
//
//    override fun validate(): Boolean {
//        return conditions.all {
//            isValid().apply { if (!this) onError(it.errorMessage(it.value())) }
//        }
//    }
//}