package com.denchic45.studiversity.ktor

import com.denchic45.studiversity.api.GeneralErrors


class ForbiddenException(message: String? = GeneralErrors.PERMISSION_DENIED) : Exception(message)