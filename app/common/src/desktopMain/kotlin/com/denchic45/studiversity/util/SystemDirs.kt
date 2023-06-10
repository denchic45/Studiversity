package com.denchic45.studiversity.util

import net.harawata.appdirs.AppDirsFactory
import okio.Path
import okio.Path.Companion.toPath

actual class SystemDirs actual constructor() {
    actual val appDir: Path
        get() = appPath.toPath()

    actual val fileDir: Path
        get() = appDir / "files"

    actual val prefsDir: Path
        get() = appDir / "preferences"

    companion object {
        val appPath: String = AppDirsFactory
            .getInstance()
            .getUserDataDir("Studiversity", null, null, true)
    }
}