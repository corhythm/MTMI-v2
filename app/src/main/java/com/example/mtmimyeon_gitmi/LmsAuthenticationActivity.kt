package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mtmimyeon_gitmi.databinding.ActivityLmsAuthenticationBinding

class LmsAuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLmsAuthenticationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLmsAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}