package com.denchic45.kts.util

import android.content.Context
import java.io.File
import javax.inject.Inject

actual class SystemDirs  actual constructor() {

    lateinit var context: Context
        private set

    @Inject
    constructor(context: Context) : this() {
        this.context = context
    }

    actual val appDirectory: File
        get() = context.applicationContext.filesDir

    actual val prefsDirectory: File
        get() = File("shared_prefs").relativeTo(appDirectory)
}