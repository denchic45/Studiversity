package com.denchic45.kts.util

import android.content.Context
import okio.Path.Companion.toOkioPath
import java.io.File

actual class SystemDirs actual constructor() {
    lateinit var context: Context
        private set


    constructor(context: Context) : this() {
        this.context = context
    }

    actual val appDir: File
        get() {
            return context.filesDir!!.parentFile!!
        }

    actual val fileDir: File
        get()  = context.filesDir!!

    actual val prefsDir: File
        get() = (appDir.toOkioPath() / "shared_prefs").toFile()
}