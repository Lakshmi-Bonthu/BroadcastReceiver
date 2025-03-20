package com.example.broadcastreceiver.fragments



import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import java.io.File

import android.media.MediaPlayer
import com.example.broadcastreceiver.R


class PlaybackFragment : Fragment() {

    private lateinit var recordedFilesListView: ListView
    private lateinit var recordings: List<File>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_playback, container, false)
        recordedFilesListView = view.findViewById(R.id.recordedFilesListView)

        // Fetch recorded files
        val audioDir = requireContext().filesDir
        recordings = audioDir.listFiles { file -> file.extension == "3gp" }?.toList() ?: emptyList()

        // Show files in the ListView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recordings.map { it.name })
        recordedFilesListView.adapter = adapter

        // Handle playback
        recordedFilesListView.setOnItemClickListener { _, _, position, _ ->
            val filePath = recordings[position].absolutePath
            playAudio(filePath)
        }

        return view
    }

    private fun playAudio(filePath: String) {
        try {
            val mediaPlayer = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
            }
            Toast.makeText(context, "Playing: ${File(filePath).name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error playing audio: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
