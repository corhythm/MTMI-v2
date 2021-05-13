package com.example.mtmimyeon_gitmi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mtmimyeon_gitmi.CopyrightActivity
import com.example.mtmimyeon_gitmi.chatting.ChattingRoomListActivity
import com.example.mtmimyeon_gitmi.databinding.ActivityMyProfileBinding

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.textViewMyProfileGoToCopyright.setOnClickListener {
            startActivity(Intent(this, CopyrightActivity::class.java))
        }

        binding.textViewMyProfileGoToChat.setOnClickListener {
            startActivity(Intent(this, ChattingRoomListActivity::class.java))
        }
    }
}