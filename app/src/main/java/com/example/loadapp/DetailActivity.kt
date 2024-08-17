package com.example.loadapp

import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.loadapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        ViewCompat.setOnApplyWindowInsetsListener(binding.detail) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()

        binding.okButton.setOnClickListener {
            finish()
        }

        binding.filename.text = intent.getStringExtra("name")
        val status = if(intent.getBooleanExtra("status", false)) {
            resources.getString(R.string.success)
        } else {
            resources.getString(R.string.failed)
        }
        binding.status.text = status

        binding.motionLayout.post {
            binding.motionLayout.transitionToEnd()
        }
    }
}