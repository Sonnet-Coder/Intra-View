package com.eventapp.intraview

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EventApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}


