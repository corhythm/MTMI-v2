package com.mju.mtmi.myClass

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.mju.mtmi.R
import com.mju.mtmi.databinding.ActivityMyClassSubjectBulletinBoardWritingBinding
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager
import com.google.firebase.auth.FirebaseAuth

class MyClassSubjectBulletinBoardWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassSubjectBulletinBoardWritingBinding
    private var db = FirebaseManager()
    private var auth = FirebaseAuth.getInstance()
    lateinit var subjectName: String
    lateinit var idx: String
    override fun onCreate(savedInstanceState: Bundle?) {

        subjectName = intent.getStringExtra("과목이름")!! // 과목 이름
        idx = intent.getStringExtra("과목코드")!! // 과목 코드
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassSubjectBulletinBoardWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fabMyClassSubjectBulletinBoardPost.setOnClickListener {
            uploadPost()
            val intent = Intent()
            intent.putExtra("upload", true)
            setResult(Activity.RESULT_OK, intent)
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
        when (item.itemId) {
            else -> {
            }
        }
        return true
    }

    private fun uploadPost() {
        val writer = auth.currentUser!!.uid
        val title = binding.editTextMyClassSubjectBulletinBoardWritingTitle.text.toString()
        val content = binding.editTextMyClassSubjectBulletinBoardWritingContent.text.toString()
        db.writePost(idx, writer, title, content, object : DataBaseCallback<Boolean> {
            override fun onCallback(data: Boolean) {
                if (data) {
                    Log.d("포스트업로드", "성공")
                }
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}