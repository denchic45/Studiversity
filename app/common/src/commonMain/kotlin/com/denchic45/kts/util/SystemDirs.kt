package com.denchic45.kts.util

import java.io.File

expect class SystemDirs() {
    val appDirectory: File
    val prefsDirectory: File
}