package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mtmimyeon_gitmi.databinding.ActivityCampusPhoneNumberBinding

class CampusPhoneNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCampusPhoneNumberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCampusPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}