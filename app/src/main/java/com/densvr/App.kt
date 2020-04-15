package com.densvr

import androidx.multidex.MultiDexApplication
import timber.log.Timber

/**
 * Created by i-sergeev on 07.04.2020.
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}