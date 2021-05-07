package com.example.mtmimyeon_gitmi.util

import android.app.Application

// 전역 Context
class App: Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}