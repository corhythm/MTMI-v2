package com.example.mtmimyeon_gitmi.util

import android.app.Application
import android.util.Log

// 전역 Context
class App: Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d("로그", "App -onCreate() called // app 초기화")
    }
}