package com.denchic45.kts.domain.error


sealed interface CommonError
object NetworkError : CommonError, SearchError<Nothing>