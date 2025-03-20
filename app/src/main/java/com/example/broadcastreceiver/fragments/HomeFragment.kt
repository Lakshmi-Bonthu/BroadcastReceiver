package com.example.broadcastreceiver.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.broadcastreceiver.R

import java.io.File

class HomeFragment : Fragment() {

    private lateinit var audioSettingsTextView: TextView
    private lateinit var playbackCallTextView: TextView
    private lateinit var bluetoothTextView: TextView
    private lateinit var microphoneButton: ImageButton

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String = ""
    private var isRecording = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Views
        audioSettingsTextView = view.findViewById(R.id.audioSettingsTextView)
        playbackCallTextView = view.findViewById(R.id.playbackCallTextView)
        bluetoothTextView = view.findViewById(R.id.bluetoothTextView)
        microphoneButton = view.findViewById(R.id.microphoneButton)

        // Navigate to Audio Settings Fragment
        audioSettingsTextView.setOnClickListener {
            navigateToFragment(AudioSettingFragment())
        }

        // Navigate to Playback Call Fragment
        playbackCallTextView.setOnClickListener {
            navigateToFragment(PlaybackFragment())
        }

        // Navigate to Bluetooth Fragment
        bluetoothTextView.setOnClickListener {
            navigateToFragment(BluetoothFragment())
        }

        // Microphone Button Click for Recording
        microphoneButton.setOnClickListener {
            if (!isRecording) {
                startRecording()
            } else {
                stopRecording()
            }
        }

        return view
    }

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1001
            )
            return
        }

        val audioDir = requireContext().filesDir
        val audioFile = File.createTempFile("audio_", ".3gp", audioDir)
        audioFilePath = audioFile.absolutePath

        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(audioFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }
            isRecording = true
            microphoneButton.setImageResource(R.drawable.ic_stop)
            Toast.makeText(context, "Recording started...", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Recording failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            microphoneButton.setImageResource(R.drawable.ic_microphone)
            Toast.makeText(context, "Recording saved at $audioFilePath", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to stop recording: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
