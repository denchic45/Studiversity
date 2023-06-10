package com.denchic45.studiversity.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun SyncedEffect(
    key1: Any?,
    block: () -> Unit,
) {
    remember(key1) { block() }
}

//class SyncedEffect(
//    private val task: () -> Unit,
//) : RememberObserver {
//
//    override fun onRemembered() {
//        task()
//    }
//
//    override fun onForgotten() {
//
//    }
//
//    override fun onAbandoned() {
//
//    }
//}