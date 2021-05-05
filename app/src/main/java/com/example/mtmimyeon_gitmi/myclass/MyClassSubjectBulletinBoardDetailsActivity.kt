package com.example.mtmimyeon_gitmi.myClass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardDetailsBinding

class MyClassSubjectBulletinBoardDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}