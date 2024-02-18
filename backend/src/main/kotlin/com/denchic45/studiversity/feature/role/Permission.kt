package com.denchic45.studiversity.feature.role

enum class Permission {
    UNDEFINED, ALLOW, PROHIBIT
}

fun List<Permission>.combinedPermission(): Permission {
    if (isEmpty()) return Permission.UNDEFINED
    if (size == 1) return single()
    return maxOf { it }
}