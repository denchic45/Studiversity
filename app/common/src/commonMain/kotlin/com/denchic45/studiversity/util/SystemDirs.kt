package com.denchic45.studiversity.util

import okio.Path

expect class SystemDirs() {
    val appDir: Path
    val fileDir: Path
    val prefsDir: Path
}