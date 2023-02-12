package com.studiversity.feature.role

enum class Permission {
    Undefined, Allow, Prohibit
}

fun List<Permission>.combinedPermission(): Permission {
    if (isEmpty()) return Permission.Undefined
    if (size == 1) return single()
    return maxOf { it }
}