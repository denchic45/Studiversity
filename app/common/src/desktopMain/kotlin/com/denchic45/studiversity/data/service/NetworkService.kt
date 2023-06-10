package com.denchic45.studiversity.data.service

import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
actual class NetworkService {
    actual val isNetworkAvailable: Boolean
        get() = true
    actual fun observeNetwork(): Flow<Boolean> {
        TODO("Not yet implemented")
    }
}