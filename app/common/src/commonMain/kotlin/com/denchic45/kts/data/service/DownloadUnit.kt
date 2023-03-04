package com.denchic45.kts.data.service

import com.denchic45.kts.data.domain.model.FileState
import kotlinx.coroutines.flow.Flow

expect class DownloadUnit private constructor() {
    val state: Flow<FileState>

    companion object {
        fun startWork():DownloadUnit
        fun get(fileName: String):DownloadUnit
    }
}

//sealed class DownloadState {
//    object Begin : DownloadState()
//    class Downloading(progress: Int) : DownloadState()
//    object Done : DownloadState()
//    object Failed : DownloadState()
//}