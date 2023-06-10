package com.denchic45.studiversity.ktor

import com.denchic45.studiversity.feature.role.RoleErrors


class ForbiddenException(message: String? = RoleErrors.PERMISSION_DENIED) : Exception(message)