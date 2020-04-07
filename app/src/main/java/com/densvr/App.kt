package com.densvr

import android.app.Application
import timber.log.Timber

/**
 * Created by i-sergeev on 07.04.2020.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}