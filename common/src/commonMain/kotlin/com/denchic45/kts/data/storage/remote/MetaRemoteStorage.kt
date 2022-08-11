package com.denchic45.kts.data.storage.remote

expect class MetaRemoteStorage {
    suspend  fun getUrl():String
}