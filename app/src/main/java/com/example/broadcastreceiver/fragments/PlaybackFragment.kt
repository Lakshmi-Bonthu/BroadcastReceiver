package com.example.broadcastreceiver.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.broadcastreceiver.R
import java.io.File

class PlaybackFragment : Fragment() {

    private lateinit var recordedFilesListView: ListView
    private lateinit var recordings: MutableList<File> // MutableList to allow modifications

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_playback, container, false
        )
        recordedFilesListView = view.findViewById(R.id.recordedFilesListView)

        // Fetch recorded files from internal storage
        val audioDir = requireContext().filesDir
        recordings = audioDir.listFiles { file -> file.extension == "3gp" }?.toMutableList() ?: mutableListOf()

        // Set up a custom adapter for the ListView
        recordedFilesListView.adapter = object : BaseAdapter() {
            override fun getCount() = recordings.size
            override fun getItem(position: Int) = recordings[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val itemView = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_item_recording, parent, false)
                val file = recordings[position]

                val fileNameEditText: EditText = itemView.findViewById(R.id.fileNameEditText)
                val editButton: ImageButton = itemView.findViewById(R.id.editButton)
                val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
                val downloadButton: ImageButton = itemView.findViewById(R.id.downloadButton)

                // Set the file name in the EditText
                fileNameEditText.setText(file.nameWithoutExtension)

                // Handle Edit Button
                editButton.setOnClickListener {
                    if (fileNameEditText.isFocusable) {
                        // Save the file
                        saveEditedFileName(file, fileNameEditText, position)
                        toggleEditing(fileNameEditText, false)
                        editButton.setImageResource(android.R.drawable.ic_menu_edit) // Change back to Edit icon
                    } else {
                        // Enable editing
                        toggleEditing(fileNameEditText, true)
                        editButton.setImageResource(android.R.drawable.ic_menu_save) // Change to Save icon
                    }
                }

                // Handle Delete Button
                deleteButton.setOnClickListener {
                    confirmAndDeleteFile(file, position)
                }

                // Handle Download Button
                downloadButton.setOnClickListener {
                    startDownloadService(file.absolutePath)
                }

                // Handle playback
                itemView.setOnClickListener {
                    startPlaybackService(file.absolutePath)
                }

                return itemView
            }
        }

        return view
    }

    private fun toggleEditing(fileNameEditText: EditText, isEditable: Boolean) {
        fileNameEditText.isFocusable = isEditable
        fileNameEditText.isFocusableInTouchMode = isEditable
        fileNameEditText.background = if (isEditable) null else context?.getDrawable(android.R.color.transparent)
    }

    private fun saveEditedFileName(file: File, fileNameEditText: EditText, position: Int) {
        val newFileName = fileNameEditText.text.toString()
        if (newFileName.isNotBlank()) {
            val newFile = File(file.parent, "$newFileName.3gp")
            if (file.renameTo(newFile)) {
                recordings[position] = newFile
                (recordedFilesListView.adapter as BaseAdapter).notifyDataSetChanged()
                Toast.makeText(context, "File renamed to $newFileName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error renaming file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "File name cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmAndDeleteFile(file: File, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete File")
            .setMessage("Are you sure you want to delete ${file.name}?")
            .setPositiveButton("Yes") { _, _ ->
                if (file.delete()) {
                    recordings.removeAt(position)
                    (recordedFilesListView.adapter as BaseAdapter).notifyDataSetChanged()
                    Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error deleting file", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun startPlaybackService(filePath: String) {
        val intent = Intent(requireContext(), com.example.broadcastreceiver.services.MediaPlaybackService::class.java).apply {
            putExtra("filePath", filePath)
        }
        requireContext().startService(intent)
        Toast.makeText(context, "Playing: ${File(filePath).name}", Toast.LENGTH_SHORT).show()
    }

    private fun startDownloadService(filePath: String) {
        val intent = Intent(requireContext(), com.example.broadcastreceiver.services.FileDownloadService::class.java).apply {
            putExtra("filePath", filePath)
        }
        requireContext().startService(intent)
        Toast.makeText(context, "Downloading: ${File(filePath).name}", Toast.LENGTH_SHORT).show()
    }
}
