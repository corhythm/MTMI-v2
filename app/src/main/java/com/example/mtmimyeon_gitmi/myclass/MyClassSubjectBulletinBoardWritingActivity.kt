package com.example.mtmimyeon_gitmi.myclass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardWritingBinding

class MyClassSubjectBulletinBoardWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        setSupportActionBar(binding.toolbarMyClassSubjectBulletinBoardToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_my_class_subject_bulletin_board_writing_back)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // toolbar inflate
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_my_class_subject_bulletin_board_writing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.homeAsUp -> {
                finish()
                Log.d("로그", "MyClassSubjectBulletinBoardWritingActivity -onOptionsItemSelected() called")
            }
            R.id.menu_toolBar_search -> {
                finish()
                Log.d("로그", "MyClassSubjectBulletinBoardWritingActivity -onOptionsItemSelected() called")
            }
            else -> { }
        }
        return true
    }
}