package com.denchic45.studiversity.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.denchic45.studiversity.ui.model.AttachmentItem
import java.io.File

class AttachmentViewer(private val activity: Activity, private val onNotFoundActivity: () -> Unit) {

    private fun openFile(file: File) {
        // Get URI and MIME type of file
        // Open file with user selected app
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            val apkURI = FileProvider.getUriForFile(
                activity,
                activity.applicationContext.packageName.toString() + ".provider",
                file
            )
            val extensionFromMimeType = MimeTypeMap
                .getSingleton()
                .getExtensionFromMimeType(file.toURI().toURL().toString())
            setDataAndType(apkURI, extensionFromMimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        open(intent)
    }

    fun openAttachment(item:AttachmentItem) {
        when(item) {
            is AttachmentItem.FileAttachmentItem -> openFile(item.path.toFile())
            is AttachmentItem.LinkAttachmentItem -> openLink(Uri.parse(item.url))
        }
    }

    fun openLink(uri: Uri) {
        // Get URI and MIME type of file
        // Open file with user selected app
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
        }
        open(intent)
    }

    private fun open(intent: Intent) {
        try {
            activity.startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            onNotFoundActivity()
        }
    }
}