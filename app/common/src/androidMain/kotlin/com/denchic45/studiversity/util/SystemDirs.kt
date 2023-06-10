package com.denchic45.studiversity.util

import android.content.Context
import okio.Path
import okio.Path.Companion.toOkioPath

actual class SystemDirs actual constructor() {
    lateinit var context: Context
        private set


    constructor(context: Context) : this() {
        this.context = context
    }

    actual val appDir: Path
        get() {
            return context.filesDir!!.parentFile!!.toOkioPath()
        }

    actual val fileDir: Path
        get() = context.filesDir!!.toOkioPath()

    actual val prefsDir: Path
        get() = appDir / "shared_prefs"
}