package com.denchic45.kts.util

import java.io.File

expect class SystemDirs() {
    val appDirectory: File
    val prefsDirectory: File
}

val SystemDirs.databaseFile: File
    get() = File(File.separator + "databases" + "database.db").relativeTo(appDirectory)