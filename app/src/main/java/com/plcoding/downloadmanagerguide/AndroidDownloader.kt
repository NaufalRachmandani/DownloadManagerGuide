package com.plcoding.downloadmanagerguide

import android.app.DownloadManager
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log

class AndroidDownloader(
    private val context: Context,
) {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    val scanUri: String = "content://downloads/my_downloads"
    val url =
        "https://d25huoxj1n3heg.cloudfront.net/buku/data/KEBEBASAN-FILSAFAT-POLITIK-HERRYP_enc.pdf"

    fun downloadFile(
        status: (Long, Long, Long) -> Unit = { downloaded, total, percentage -> },
        observer: (ContentObserver) -> Unit = {},
    ): Long {
        val request = DownloadManager.Request(Uri.parse(this.url)).apply {
            setTitle("downloading video")
            setDescription("Please Wait")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename")
        }

        val id = downloadManager.enqueue(request)

        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)

                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor != null && cursor.moveToFirst()) {
                    val downloaded =
                        cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))

                    val total =
                        cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    val percentage = (downloaded * 100) / total

                    status(downloaded, total, percentage)
                }
            }
        }

        observer(contentObserver)

        return id
    }

    fun registerObserver(observer: ContentObserver?) {
        if (observer != null) {
            context.contentResolver.registerContentObserver(
                Uri.parse(scanUri),
                true,
                observer,
            )
        }
        Log.e(
            "CobaDownloadManager",
            "registerContentObserver",
        )
    }

    fun unregisterObserver(observer: ContentObserver?) {
        if (observer != null) {
            context.contentResolver.unregisterContentObserver(
                observer,
            )
        }

        Log.e(
            "CobaDownloadManager",
            "un registerObserver",
        )
    }
}
