package com.denchic45.kts.data.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.denchic45.kts.data.storage.Storage
import com.denchic45.kts.data.storage.TestStorage
import com.github.michaelbull.result.fold

abstract class DownloadWorker<T : Storage>(
    context: Context,
    params: WorkerParameters,
    private val storage: T,
) : CoroutineWorker(context, params) {
    companion object {
        const val CHANNEL_ID = "DOWNLOAD_FILES"
    }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        return storage.downloadAndSave()
            .fold(success = { Result.success() }, failure = { Result.failure() })
    }


    private fun createForegroundInfo(): ForegroundInfo {
        val title = "Загрузка файлов"
        val notification = NotificationCompat.Builder(
            applicationContext,
            createNotificationChannel()
        ).setContentTitle(title)
            .setTicker(title)
            .setProgress(0, 0, true)
            .setSmallIcon(com.denchic45.common.R.drawable.arrow_drop_down)
            .setOngoing(true)
            .build()
        return ForegroundInfo(1, notification)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo()
    }

    private fun createNotificationChannel(): String {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Download files",
                NotificationManager.IMPORTANCE_NONE
            )
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
        return CHANNEL_ID
    }
}

class DownloadTestWorker(context: Context, params: WorkerParameters, testStorage: TestStorage) :
    DownloadWorker<TestStorage>(context, params, testStorage)