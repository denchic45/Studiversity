package com.denchic45.kts.domain.error


sealed interface CommonError:SearchError
object NetworkError : SearchError, CommonError