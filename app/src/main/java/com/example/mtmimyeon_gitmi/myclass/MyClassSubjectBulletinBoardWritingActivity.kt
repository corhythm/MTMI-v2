package com.example.mtmimyeon_gitmi.myClass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassSubjectBulletinBoardWritingBinding
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.google.firebase.auth.FirebaseAuth

class MyClassSubjectBulletinBoardWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardWritingBinding
    private var DB = DatabaseManager()
    private var auth = FirebaseAuth.getInstance()
    var subjectName: String = intent.getStringExtra("과목이름") // 과목 이름
    var idx: String = intent.getStringExtra("과목코드")// 과목 코드

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fabMyClassSubjectBulletinBoardPost.setOnClickListener{
            uploadPost()
            finish()
        }
    }

    private fun init() {
//        setSupportActionBar(binding.bottomAppBarMyClassSubjectBulletinBoardBottomAppBar)
        setSupportActionBar(binding.bottomAppBarMyClassSubjectBulletinBoardBottomAppBar)

        // 네비게이션 아이콘 클릭 -> 글 쓰기 취소
        binding.toolbarMyClassSubjectBulletinBoardToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // toolbar inflate
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.bottomappbar_my_class_subject_bulletin_board_writing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            else -> { }
        }
        return true
    }
    fun uploadPost() {
        var writter = auth.currentUser.uid
        var title = binding.editTextMyClassSubjectBulletinBoardWritingTitle.text.toString()
        var content = binding.editTextMyClassSubjectBulletinBoardWritingContent.text.toString()
        DB.writePost(idx,subjectName,writter, title, content)
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}