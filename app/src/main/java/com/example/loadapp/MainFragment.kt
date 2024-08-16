package com.example.loadapp

import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.loadapp.databinding.MainFragmentBinding

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        binding.viewModel = viewModel

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        viewModel.downloadStarted.observe(viewLifecycleOwner) { downloadStarted ->
            if (downloadStarted) {
                val animator = ObjectAnimator.ofFloat(binding.downloadButton, "progress", 0f, 1f)
                animator.duration = 1000
                animator.start()
            }
        }

        return binding.root
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

        val notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}