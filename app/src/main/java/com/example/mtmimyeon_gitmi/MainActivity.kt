package com.example.mtmimyeon_gitmi

import android.net.http.HttpResponseCache
import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityMainBinding

import org.jsoup.Connection
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val activityMainBinding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)// 자동으로 완성된 Activity Main Binding 클래스를 인스턴스로 가져온다.

        // 뷰바인딩과 연결
        setContentView(activityMainBinding.root)
    }
}

