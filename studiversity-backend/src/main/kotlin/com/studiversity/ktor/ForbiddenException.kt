package com.studiversity.ktor

import com.studiversity.feature.role.RoleErrors


class ForbiddenException(message: String? = RoleErrors.PERMISSION_DENIED) : Exception(message)