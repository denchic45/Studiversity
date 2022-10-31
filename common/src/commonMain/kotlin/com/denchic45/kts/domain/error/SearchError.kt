package com.denchic45.kts.domain.error

sealed interface SearchError<T>

object NotFound : SearchError<Nothing>