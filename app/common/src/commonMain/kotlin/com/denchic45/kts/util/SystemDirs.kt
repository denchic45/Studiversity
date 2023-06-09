package com.denchic45.kts.util

import okio.Path

expect class SystemDirs() {
    val appDir: Path
    val fileDir: Path
    val prefsDir: Path
}