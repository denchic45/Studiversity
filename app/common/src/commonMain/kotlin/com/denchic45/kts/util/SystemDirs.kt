package com.denchic45.kts.util

import java.io.File

expect class SystemDirs() {
    val appDir: File
    val fileDir:File
    val prefsDir: File
}