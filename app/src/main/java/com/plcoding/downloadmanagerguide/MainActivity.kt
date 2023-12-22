package com.plcoding.downloadmanagerguide

import android.database.ContentObserver
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.plcoding.downloadmanagerguide.ui.theme.DownloadManagerGuideTheme

class MainActivity : ComponentActivity() {
    var seekBarValue = mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DownloadManagerGuideTheme {
                seekBarValue = remember {
                    mutableStateOf(0f)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Slider(value = (seekBarValue.value) / 100, onValueChange = {
                        seekBarValue.value = it
                    })

                    Button(onClick = {
                        downloadMedia()
                    }) {
                        Text(text = "Download")
                    }
                }
            }
        }
    }

    private fun downloadMedia() {
        val downloader = AndroidDownloader(this)
        var observer: ContentObserver? = null
        downloader.downloadFile(
            { downloaded, total, percentage ->
                seekBarValue.value = (percentage).toFloat()
                Log.e(
                    "CobaDownloadManager",
                    "downloaded:$downloaded total:$total percentage:$percentage",
                )
                if (downloaded == total) {
                    downloader.unregisterObserver(observer)
                }
            },
            { contentObserver ->
                observer = contentObserver
                downloader.registerObserver(observer)
            },
        )
    }
}
