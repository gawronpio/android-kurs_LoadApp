package com.example.loadapp

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.DOWNLOAD_SERVICE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class MainViewModel(application: Application): AndroidViewModel(application) {
    private var selectedUrl = ""
    private var selectedName = ""
    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = 0

    private val _downloadStarted = MutableLiveData<Boolean>(false)
    val downloadStarted: LiveData<Boolean>
        get() = _downloadStarted

    fun onSelected(pos: Int) {
        when (pos) {
            0 -> {
                selectedUrl = getApplication<Application>().getString(R.string.link1_url)
                selectedName = getApplication<Application>().getString(R.string.link1_name)
            }
            1 -> {
                selectedUrl = getApplication<Application>().getString(R.string.link2_url)
                selectedName = getApplication<Application>().getString(R.string.link2_name)
            }
            2 -> {
                selectedUrl = getApplication<Application>().getString(R.string.link3_url)
                selectedName = getApplication<Application>().getString(R.string.link3_name)
            }
        }
    }

    fun onDownload() {
        if(selectedUrl.isEmpty()) {
            Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.select_file_hint), Toast.LENGTH_SHORT).show()
        } else {
            download(selectedName, selectedUrl)
            _downloadStarted.value = true
        }
    }

    private fun download(name: String, url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getApplication<Application>().getString(R.string.app_name))
                .setDescription(getApplication<Application>().getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getApplication<Application>().getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)

        registerDownloadCompleteReceiver(name)
    }

    private fun registerDownloadCompleteReceiver(name: String) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadID) {
                    val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query().setFilterById(id)
                    val cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        if (statusIndex != -1) {
                            val status = cursor.getInt(statusIndex)
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                showNotification(
                                    mapOf(
                                        "name" to getApplication<Application>().getString(
                                            R.string.download_completed,
                                            name),
                                        "status" to true
                                    )
                                )
                            } else {
                                showNotification(
                                    mapOf(
                                        "name" to getApplication<Application>().getString(
                                            R.string.download_failed,
                                            name),
                                        "status" to false
                                    )
                                )
                            }
                        }
                    }
                    cursor.close()
                    context.unregisterReceiver(this)
                }
            }
        }
        getApplication<Application>().registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun showNotification(data: Map<String, Any>) {
        val notificationManager = getApplication<Application>().getSystemService(NotificationManager::class.java)
        notificationManager?.sendNotification(
            data,
            getApplication()
        )
    }
}