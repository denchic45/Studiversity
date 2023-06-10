package com.denchic45.studiversity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher

actual val Dispatchers.PlatformMain: MainCoroutineDispatcher
    get() = Dispatchers.Main