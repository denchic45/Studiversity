package com.denchic45.kts.domain.error


interface Cause {
    object NetworkError : Cause
    object ServerError : Cause
}