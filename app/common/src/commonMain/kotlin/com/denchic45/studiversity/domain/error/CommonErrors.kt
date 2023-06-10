package com.denchic45.studiversity.domain.error


interface Cause {
    object NetworkError : Cause
    object ServerError : Cause
}