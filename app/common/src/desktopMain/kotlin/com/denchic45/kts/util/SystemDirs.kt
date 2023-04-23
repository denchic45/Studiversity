package com.denchic45.kts.util

import net.harawata.appdirs.AppDirsFactory
import okio.Path.Companion.toOkioPath
import java.io.File

actual class SystemDirs actual constructor() {
    actual val appDir: File
        get() = File(appPath)

    actual val fileDir:File
        get() = (appDir.toOkioPath() / "files").toFile()

    actual val prefsDir: File
        get() = File("preferences").relativeTo(appDir)

    companion object {
        val appPath: String = AppDirsFactory
            .getInstance()
            .getUserDataDir("Studiversity", null, null, true)
    }
}