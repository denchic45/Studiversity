package com.denchic45.kts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.swing.Swing

actual val Dispatchers.PlatformMain: MainCoroutineDispatcher
    get() = Dispatchers.Swing