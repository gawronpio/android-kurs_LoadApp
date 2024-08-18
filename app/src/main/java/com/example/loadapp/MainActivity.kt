package com.example.loadapp

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.loadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.viewModel = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionGranted = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!notificationPermissionGranted) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATIONS
                )
            }
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        viewModel.downloadStarted.observe(this) { downloadStarted ->
            if (downloadStarted) {
                val animator = ObjectAnimator.ofFloat(binding.downloadButton, "progress", 0f, 1f)
                animator.duration = 1000
                animator.repeatMode = ValueAnimator.RESTART
                animator.repeatCount = ValueAnimator.INFINITE
                animator.start()
                viewModel.animator = animator
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_NOTIFICATIONS = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_NOTIFICATIONS) {
            if((grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, getString(R.string.notifications_prohibited), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = "@string/download_complete"

        val notificationManager = this.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}