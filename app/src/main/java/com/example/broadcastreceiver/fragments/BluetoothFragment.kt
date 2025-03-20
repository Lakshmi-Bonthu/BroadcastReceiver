package com.example.broadcastreceiver.fragments



import android.Manifest

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.broadcastreceiver.R


class BluetoothFragment : Fragment() {

    private lateinit var bluetoothStatusTextView: TextView
    private lateinit var turnOnBluetoothButton: Button
    private lateinit var turnOffBluetoothButton: Button
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    companion object {
        private const val REQUEST_ENABLE_BT = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bluetooth, container, false)

        // Initialize Views
        bluetoothStatusTextView = view.findViewById(R.id.bluetoothStatusTextView)
        turnOnBluetoothButton = view.findViewById(R.id.turnOnBluetoothButton)
        turnOffBluetoothButton = view.findViewById(R.id.turnOffBluetoothButton)

        // Update initial Bluetooth status
        updateBluetoothStatus()

        // Handle Turn On Bluetooth
        turnOnBluetoothButton.setOnClickListener {
            checkAndRequestPermission { enableBluetooth() }
        }

        // Handle Turn Off Bluetooth
        turnOffBluetoothButton.setOnClickListener {
            checkAndRequestPermission { disableBluetooth() }
        }

        return view
    }

    private fun updateBluetoothStatus() {
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothStatusTextView.text = "Bluetooth is On"
        } else {
            bluetoothStatusTextView.text = "Bluetooth is Off"
        }
    }

    private fun checkAndRequestPermission(action: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_ENABLE_BT
            )
        } else {
            action()
        }
    }

    private fun enableBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
        } else if (bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth is already On", Toast.LENGTH_SHORT).show()
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        updateBluetoothStatus()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun disableBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show()
        } else if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Bluetooth is already Off", Toast.LENGTH_SHORT).show()
        } else {
            bluetoothAdapter.disable()
            updateBluetoothStatus()
            Toast.makeText(context, "Turning Off Bluetooth...", Toast.LENGTH_SHORT).show()
        }
    }
}
