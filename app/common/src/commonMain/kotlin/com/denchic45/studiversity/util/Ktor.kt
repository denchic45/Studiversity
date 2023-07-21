package com.denchic45.studiversity.util

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.util.InternalAPI
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min


// TODO: использовать для скачивания файлов по частям
@OptIn(InternalAPI::class)
suspend fun HttpClient.download(url: String, outFile: File, chunkSize: Int = 1024) {
    val length = head(url).headers[HttpHeaders.ContentLength]?.toLong() as Long
    val lastByte = length - 1

    var start = outFile.length()
    val output = withContext(Dispatchers.IO) {
        FileOutputStream(outFile, true)
    }

    while (true) {
        val end = min(start + chunkSize - 1, lastByte)

        get(url) {
            header("Range", "bytes=${start}-${end}")
        }.content.copyTo(output)

        if (end >= lastByte) break

        start += chunkSize
    }
}