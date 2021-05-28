package com.example.mtmimyeon_gitmi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.mtmimyeon_gitmi.CopyrightActivity
import com.example.mtmimyeon_gitmi.R
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

        // copyright 이동
        binding.textViewMyProfileGoToCopyright.setOnClickListener {
            Intent(this, CopyrightActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }

        // 채팅 보관함 이동
        binding.textViewMyProfileGoToChat.setOnClickListener {
            Intent(this, ChattingRoomListActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }

        // AppbarLayout elevation = 0dp
        binding.appLayoutMyProfileAppbarLayout.outlineProvider = null


        // toolbar appbar로 지정
        setSupportActionBar(binding.toolbarMyProfileToolbar)

        // 뒤로 가기 눌렀을 때
        binding.toolbarMyProfileToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_account_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("로그", "MyProfileActivity -onOptionsItemSelected() called")
        when (item.itemId) {
            R.id.menu_edit_account -> { // 프로필 수정 메뉴 누르면 EditProfileActivity로 이동
                Intent(this, EditProfileActivity::class.java).also {
                    startActivity(it)
                    overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
                }
            }
        }
        return true
    }
}