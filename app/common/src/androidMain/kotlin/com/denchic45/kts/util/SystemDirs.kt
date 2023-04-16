package com.denchic45.kts.util

import android.content.Context
import me.tatarka.inject.annotations.Inject
import java.io.File


actual class SystemDirs actual constructor() {
    lateinit var context: Context
        private set
    @Inject
    constructor(context: Context) : this() {
        this.context = context
    }

    actual val appDirectory: File
        get() = context.applicationContext.dataDir

    actual val prefsDirectory: File
        get() = File("shared_prefs").relativeTo(appDirectory)
}