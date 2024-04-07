package com.denchic45.studiversity.domain.resource

import com.denchic45.studiversity.util.ErrorResponse

sealed class Failure
data object NoConnection : Failure()
class ClientError(val response: ErrorResponse) : Failure()
class Cause(val throwable: Throwable) : Failure()