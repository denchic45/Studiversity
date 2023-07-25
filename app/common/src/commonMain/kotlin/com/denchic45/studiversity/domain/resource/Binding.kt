package com.denchic45.studiversity.domain.resource


inline fun <T> bindResources(crossinline block: ResourceBinding.() -> T): Resource<T> {
    val receiver = ResourceBindingImpl()
    return try {
        with(receiver) { resourceOf(block()) }
    } catch (ex: ErrorBindException) {
        resourceOf(ex.failure)
    } catch (ex: LoadingBindException) {
        resourceOf()
    }
}

suspend inline fun <T> suspendBindResources(crossinline block: suspend ResourceBinding.() -> T): Resource<T> {
    val receiver = ResourceBindingImpl()
    return try {
        with(receiver) { resourceOf(block()) }
    } catch (ex: ErrorBindException) {
        resourceOf(ex.failure)
    } catch (ex: LoadingBindException) {
        resourceOf()
    }
}

interface ResourceBinding {
    fun <T> Resource<T>.bind(): T
}

class ResourceBindingImpl : ResourceBinding {

    override fun <T> Resource<T>.bind(): T {
        return when (val resource = this) {
            is Resource.Success -> resource.value
            Resource.Loading -> throw LoadingBindException
            is Resource.Error -> throw ErrorBindException(resource.failure)
        }
    }
}


open class BindException : Exception()

object LoadingBindException : BindException()

class ErrorBindException(val failure: Failure) : BindException()