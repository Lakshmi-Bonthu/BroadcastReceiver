package com.example.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.broadcastreceiver.fragments.GroupsFragment
import com.example.broadcastreceiver.fragments.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var networkChangeReceiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the toolbar and set it as the action bar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment and toolbar title to HomeFragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment(), "Home")
        }

        // Handle Bottom Navigation Item Selection
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment(), "Home")
                    true
                }
                R.id.nav_groups -> {
                    replaceFragment(GroupsFragment(), "Groups")
                    true
                }
                else -> false
            }
        }

        // Initialize and register the network change receiver
        networkChangeReceiver = NetworkChangeReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the network change receiver to avoid memory leaks
        unregisterReceiver(networkChangeReceiver)
    }

    // Function to replace the current fragment and update the toolbar title
    private fun replaceFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        supportActionBar?.title = title // Update the toolbar title
    }

    // Inner class to handle network connectivity changes
    class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            if (networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ) {
                Toast.makeText(context, "Network Connected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No Network Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
