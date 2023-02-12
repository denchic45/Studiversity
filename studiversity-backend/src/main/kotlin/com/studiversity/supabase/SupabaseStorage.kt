package com.studiversity.supabase

import io.github.jan.supabase.storage.BucketApi

suspend fun BucketApi.deleteRecursive(path: String) {
    val files = list(path)
    if (files.isEmpty())
        delete(path)
    else
        files.forEach {
            val filePath = "$path/${it.name}"
            if (it.id != null)
                delete(filePath)
            else
                deleteRecursive(filePath)
        }
}