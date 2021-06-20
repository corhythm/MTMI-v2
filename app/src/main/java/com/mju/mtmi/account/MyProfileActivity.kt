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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
import java.lang.Exception
import kotlin.system.exitProcess

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val currentUid = auth.uid.toString()
    private val TAG = "로그"
    private lateinit var registration: ListenerRegistration // 실시간 수신 대기 리스너

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MyProfileActivity -onStart() called")
        // 프로필 정보는 업데이트 한 후에 다시 현재 액티비티로 보여줘야 하므로 FireStoreManager Object가 아닌 현재 액티비티에서 직접 수신
        this.registration = FirebaseFirestore.getInstance().collection("users").document(currentUid)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.d(TAG, "Listen failed. $error")
                    return@addSnapshotListener
                }

                Log.d(TAG, "MyProfileActivity -onStart() called / 프로필 업데이트")
                val userData = document?.toObject(UserData::class.java)
                binding.textViewMyProfileName.text = userData!!.userName
                binding.textViewMyProfileEmail.text = userData.id
                binding.textViewMyProfileStudentIdValue.text = userData.student_id
                binding.textViewMyProfileMajorValue.text = userData.major
                binding.textViewMyProfileBirthdayValue.text = userData.birth
                binding.textViewMyProfileMbtiType.text = userData.mbtiType
                Log.d(TAG, "유저정보 업데이트 / userData = $userData")
                loadProfileImage(userData.userProfileImageUrl)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MyProfileActivity -onStop() called")
        this.registration.remove() // 실시간 리스너 해제
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
                startActivity(it)
            }
            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
        }
    }

    // 프로필 이미지 로드
    private fun loadProfileImage(profileImageUri: String) {
        try {
            FirebaseStorage.getInstance().reference.child("user_profile_images/$profileImageUri").downloadUrl.addOnSuccessListener {
                Log.d(TAG, "프로필 사진 로드/ ${it.toString()}")
                val profileImage = binding.imageViewMyProfileProfileImg
                Glide.with(applicationContext).load(it).error(R.drawable.ic_toolbar_user)
                    .circleCrop()
                    .into(profileImage)
            }.addOnFailureListener {
                Log.d(TAG, "프로필 사진 없음")
            }
        } catch (e: Exception) {
            Log.d(TAG, "MyProfileActivity -loadProfileImage() called / ${e.stackTrace}")
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
                    startActivity(it)
                }
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
        }
        return true
    }
}