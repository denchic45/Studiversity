package com.denchic45.kts.data.service

import me.tatarka.inject.annotations.Inject

@Inject
actual class NetworkService {
    actual val isNetworkAvailable: Boolean
        get() = true
}