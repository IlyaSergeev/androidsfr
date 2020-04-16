package com.densvr.ui.activities

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.densvr.androidsfr.R
import com.densvr.nfc.*
import com.densvr.nfc.ZERO_BYTE
import com.densvr.ui.viewmodels.NfcLogsViewModel
import com.densvr.ui.viewmodels.SfrRecordViewModel
import com.densvr.util.NfcReaderLogger
import com.google.android.material.navigation.NavigationView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class NavigationDrawableActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val nfcLogsViewModel: NfcLogsViewModel by viewModels()
    private val sfrRecordViewModel: SfrRecordViewModel by viewModels()

    private val readLogger by lazy {
        NfcReaderLogger()
    }

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
                R.id.nav_nfc_logs,
                R.id.nav_sfr_logs
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

        Timber.tag("NFC").d("${intent?.action}")
        if (intent?.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag?.canReadSfrRecord == true) {
//                readSfrRecodr(tag)
                writeSfrData(tag)
            }
        }
    }

    private fun writeSfrData(tag: Tag) {
        try {
            tag.writeSfrData(
                listOf(
                    SfrTagBytes(
                        3,
                        byteArrayOf(ZERO_BYTE, 0x10, ZERO_BYTE, ZERO_BYTE)
                    )
                )
            )
        } catch (error: Throwable) {
            error.printStackTrace()
        }
    }

    private fun readSfrRecodr(tag: Tag) {
        try {
            readLogger.clear()
            readLogger.appendMessage(
                SimpleDateFormat.getDateTimeInstance(
                    SimpleDateFormat.SHORT,
                    SimpleDateFormat.MEDIUM
                ).format(Date())
            )
            readLogger.appendLn()
            readLogger.appendLn()

            val sfrRecord = tag.readSfrRecord(readLogger)
            sfrRecordViewModel.setRecord(sfrRecord)
            //TODO process sfrRecord

        } catch (error: Throwable) {
            readLogger.appendMessage("\n\n")
            readLogger.appendError(error)

            //TODO set error
            sfrRecordViewModel.setRecord(null)

            error.printStackTrace()
            Toast.makeText(
                this,
                R.string.nav_drawable_activity_can_not_read_nfc,
                Toast.LENGTH_SHORT
            ).show()
        }
        nfcLogsViewModel.setLogs(readLogger.log)
    }
}
