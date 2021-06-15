package com.mju.mtmi.myClass

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.mju.mtmi.R
import com.mju.mtmi.databinding.ActivityMyClassSubjectBulletinBoardWritingBinding
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager
import com.google.firebase.auth.FirebaseAuth

// 게시판에 글쓰기
class MyClassSubjectBulletinBoardWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardWritingBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var subjectCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        this.subjectCode = intent.getStringExtra("과목코드")!! // 과목 코드

        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 글 쓰기 버튼 클릭 시 -> 게시글 등록
        binding.fabMyClassSubjectBulletinBoardPost.setOnClickListener {
            uploadMyPost()
            val intent = Intent()
            intent.putExtra("upload", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // 네비게이션 아이콘 클릭 -> 글 쓰기 취소
        binding.toolbarMyClassSubjectBulletinBoardToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // toolbar inflate
        menuInflater.inflate(R.menu.bottomappbar_my_class_subject_bulletin_board_writing, menu)
        return true
    }


    // 내가 쓴 게시글 DB에 저장장
    private fun uploadMyPost() {
        val writer = auth.currentUser!!.uid
        val title = binding.editTextMyClassSubjectBulletinBoardWritingTitle.text.toString()
        val content = binding.editTextMyClassSubjectBulletinBoardWritingContent.text.toString()
        FirebaseManager.postNewPost(this.subjectCode, writer, title, content, object : DataBaseCallback<Boolean> {
            override fun onCallback(data: Boolean) {
                if (data) {
                    Log.d("로그", "포스트업로드 성공")
                }
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}