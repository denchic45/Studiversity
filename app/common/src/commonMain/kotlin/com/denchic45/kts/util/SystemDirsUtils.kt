package com.denchic45.kts.util

import java.io.File

val SystemDirs.databaseFile: File
    get() = File(File.separator + "databases" + File.separator + "database.db")
        .relativeTo(appDir)