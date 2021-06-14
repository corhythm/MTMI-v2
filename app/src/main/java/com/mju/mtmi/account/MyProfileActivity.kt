package com.mju.mtmi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.mju.mtmi.CopyrightActivity
import com.mju.mtmi.R
import com.mju.mtmi.chatting.ChattingRoomListActivity
import com.mju.mtmi.databinding.ActivityMyProfileBinding
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager
import com.mju.mtmi.database.entity.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
import kotlin.system.exitProcess

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseManager()
    private var currentUid = auth.uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 유저 프로필 세팅
        db.callUserData(currentUid, object : DataBaseCallback<UserData> {
            override fun onCallback(data: UserData) {
                binding.textViewMyProfileName.text = data.userName
                binding.textViewMyProfileEmail.text = data.id
                binding.textViewMyProfileStudentIdValue.text = data.student_id
                binding.textViewMyProfileMajorValue.text = data.major
                binding.textViewMyProfileBirthdayValue.text = data.birth
                Log.d("로그", "data: $data")
                loadProfileImage(data.userProfileImageUrl)
            }
        })

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

        // 로그아웃 클릭했을 때
        binding.textViewMyProfileLogout.setOnClickListener {
            val mDialog = BottomSheetMaterialDialog.Builder(this)
                .setTitle("Logout?")
                .setAnimation("question.json")
                .setMessage(
                    "정말로 로그아웃 하시겠어요?",
                    TextAlignment.CENTER
                )
                .setPositiveButton("Yes") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    auth.signOut()
                    finishAffinity()
                    Intent(this, LoginActivity::class.java).also {
                        startActivity(it)
                        exitProcess(0)
                    }
                }
                .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
                .build();

            // Show Dialog
            mDialog.show();
        }

        // toolbar appbar로 지정
        setSupportActionBar(binding.toolbarMyProfileToolbar)

        // 뒤로 가기 눌렀을 때
        binding.toolbarMyProfileToolbar.setNavigationOnClickListener { finish() }

        // 프로필 이미지 클릭했을 때
        binding.imageViewMyProfileProfileImg.setOnClickListener {
            Intent(this, EditProfileActivity::class.java).also {
                startActivityForResult(it, 2000)
            }
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
    }


    // 프로필 이미지 로드
    private fun loadProfileImage(profileImageUri: String) {
        FirebaseStorage.getInstance().reference.child("user_profile_images/$profileImageUri").downloadUrl.addOnSuccessListener {
            Log.d("프로필사진 로드", it.toString())
            val profileImage = binding.imageViewMyProfileProfileImg
            Glide.with(applicationContext).load(it).error(R.drawable.ic_toolbar_user).circleCrop()
                .into(profileImage)
        }.addOnFailureListener {
            Log.d("로그", "프로필사진없음")
        }
    }

    // 프로필 이미지 바꾸고 다시 돌아왔을 때
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("로그", "MyProfileActivity -onActivityResult() called / first")
        if (requestCode == 2000) { // 프로필 업데이트
            if (resultCode == RESULT_OK) {
                Log.d(
                    "로그",
                    "MyProfileActivity -onActivityResult() called // 2000: ${data!!.getStringExtra("imgUrl")!!}"
                )
                loadProfileImage(data.getStringExtra("imgUrl")!!)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_account_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit_account -> { // 프로필 수정 메뉴 누르면 EditProfileActivity로 이동
                Intent(this, EditProfileActivity::class.java).also {
                    startActivityForResult(it, 2000)
                }
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
        return true
    }
}