package com.denchic45.kts.util

import me.tatarka.inject.annotations.Inject
import net.harawata.appdirs.AppDirsFactory
import java.io.File

actual class SystemDirs actual constructor() {
    actual val appDirectory: File
        get() = File(appPath)

    actual val prefsDirectory: File
        get() = File("preferences").relativeTo(appDirectory)

    companion object {
        val appPath: String = AppDirsFactory
            .getInstance()
            .getUserDataDir("Studiversity", null, null, true)
    }
}