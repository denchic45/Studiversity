package com.denchic45.kts.util

import net.harawata.appdirs.AppDirsFactory
import java.io.File
import javax.inject.Inject

actual class SystemDirs @Inject actual constructor() {
    actual val appDirectory: File
        get() = File(AppDirsFactory
            .getInstance()
            .getUserDataDir("KtsApp", null, null, true))
    actual val prefsDirectory: File
        get() = File("preferences").relativeTo(appDirectory)
}