package com.example.mtmimyeon_gitmi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityCopyrightBinding

class CopyrightActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCopyrightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCopyrightBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}