package com.densvr.ui.activities

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.densvr.activities.MainActivity
import com.densvr.androidsfr.R
import com.densvr.nfcreader.OldChipData
import com.densvr.nfcreader.OldGlobals
import com.densvr.nfcreader.canReadSfrRecord
import com.densvr.nfcreader.readSfrRecord
import com.google.android.material.navigation.NavigationView

class NavigationDrawableActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawable)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_names,
                R.id.nav_distances,
                R.id.nav_results,
                R.id.nav_programming,
                R.id.nav_nfc_logs
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation_drawable, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag?.canReadSfrRecord == true) {
                handleSfrNfcTag(tag)
            }
        }
    }

    private fun handleSfrNfcTag(tag: Tag) {
        try {
            val sfrRecord = tag.readSfrRecord()
            //TODO process sfrRecord
        } catch (ex: Throwable) {
            ex.printStackTrace()
            Toast.makeText(
                this,
                R.string.nav_drawable_activity_can_not_read_nfc,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
