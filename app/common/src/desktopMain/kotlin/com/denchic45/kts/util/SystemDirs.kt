package com.denchic45.kts.util

import net.harawata.appdirs.AppDirsFactory
import java.io.File
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
actual class SystemDirs @Inject actual constructor() {
    actual val appDirectory: File
        get() = File(appPath)

    actual val prefsDirectory: File
        get() = File("preferences").relativeTo(appDirectory)

    companion object {
        val appPath: String = AppDirsFactory
            .getInstance()
            .getUserDataDir("KtsApp", null, null, true)
    }
}