package com.example.mtmimyeon_gitmi.account

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityEditProfileBinding
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.example.mtmimyeon_gitmi.db.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var profileImage: Uri
    private lateinit var gender: String
    private var auth = FirebaseAuth.getInstance()
    var db = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 프로필 유저정보 초기화
        db.callUserData(auth.uid.toString(), object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                if(data != null){
                    binding.editTextActivityEditProfileIdValue.setText(data.id)
                    binding.editTextActivityEditProfileNameValue.setText(data.userName)
                    binding.editTextActivityEditProfileStudentIdValue.setText(data.student_id)
                    binding.editTextActivityEditProfileBirthValue.setText(data.birth)
                    gender= data.gender
                    loadProfileImage(data.userProfileImageUrl)
                }
            }
        })
        // 스피너에 전공 데이터 넣기
        binding.spinnerActivityEditProfileMajor.setItem(
            resources.getStringArray(R.array.major).toMutableList()
        )

        // toolbar의 뒤로 가기 눌렀을 때
        binding.toolbarActivityEditProfileToolbar.setNavigationOnClickListener { onBackPressed() }

        // 이미지 클릭 시, 로컬에서 사진 불러오기
        binding.imageViewActivityEditProfileProfileImg.setOnClickListener(this)
        binding.fabActivityEditProfileCamera.setOnClickListener(this)

        // 비밀번호, 비밀번호 확인 EditText 텍스트 변경 감지
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { // 텍스트 변경 감지
                if (binding.editTextActivityEditProfilePwValue.text.toString() == binding.editTextActivityEditProfilePwConfirmValue.text.toString()) { // 비밀번호가 일치할 때
                    binding.imageViewActivityEditProfileCheckPassword.setImageResource(R.drawable.ic_pw_check)
                } else { // 비밀번호가 일치하지 않을 때
                    binding.imageViewActivityEditProfileCheckPassword.setImageResource(R.drawable.ic_x)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // 비밀번호, 비밀번호 확인 일치여부 체크 리스너 등록
        binding.editTextActivityEditProfilePwConfirmValue.addTextChangedListener(textWatcher)
        binding.editTextActivityEditProfilePwValue.addTextChangedListener(textWatcher)



        // 업데이트 버튼 누르면 --> 프로필 업데이트
        binding.buttonActivityEditProfileUpdateProfile.setOnClickListener {
            var updateCheck = true
            var updateId = binding.editTextActivityEditProfileIdValue.text.toString()
            var updatePw=  binding.editTextActivityEditProfilePwValue.text.toString()
            var updateStudentId =binding.editTextActivityEditProfileStudentIdValue.text.toString()
            var updateName = binding.editTextActivityEditProfileNameValue.text.toString()
            var updateBirth = binding.editTextActivityEditProfileBirthValue.text.toString()
            var updateMajor :String
            if(binding.spinnerActivityEditProfileMajor.selectedItem != null){
                updateMajor = binding.spinnerActivityEditProfileMajor.selectedItem.toString()
            }else{
                updateMajor = "empty"
                updateCheck = false
                Log.d("major check","false")
            }
            if(updateCheck && (binding.editTextActivityEditProfilePwConfirmValue.text.toString() == updatePw)) {
                Log.d("update Check", "true")

                var userData = UserData(
                    updateId,
                    updatePw,
                    updateStudentId,
                    updateName,
                    updateBirth,
                    gender,
                    updateMajor,
                    profileImage.toString()
                )
                Toast.makeText(this, "유저데이터를 수정하는 중입니다", Toast.LENGTH_LONG).show()
                db.editUserData(profileImage, userData, object : Callback<Boolean> {
                    // 이미지 업로드시 callback 으로 받아오기
                    override fun onCallback(data: Boolean) {
                        if (data) {
                            Intent(this@EditProfileActivity, MyProfileActivity::class.java).also {
                                intent.putExtra("update", true)
                                setResult(Activity.RESULT_OK, intent)
                                startActivity(it)
                                overridePendingTransition(
                                    R.anim.activity_slide_in,
                                    R.anim.activity_slide_out
                                )
                                finish()
                            }
                        } else {
                            Log.d(
                                "confirm check pw ",
                                binding.editTextActivityEditProfilePwConfirmValue.text.toString()
                            )
                            Log.d("pw", updatePw)
                            Log.d("update Check", "false")
                            Toast.makeText(this@EditProfileActivity, "수정할 정보르 다시 한번 확인해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
    }
    private fun loadProfileImage(profileImageUri: String) {
        if(profileImageUri != "empty") {
            FirebaseStorage.getInstance().reference.child("image/$profileImageUri").downloadUrl.addOnSuccessListener {
                Log.d("프로필사진 로드", it.toString())
                var profileImage = binding.imageViewActivityEditProfileProfileImg
                Glide.with(applicationContext).load(it).circleCrop().into(profileImage)
            }.addOnFailureListener {
                Log.d("프로필사진 로드", "프로필사진없음")
            }
        }else{
            Log.d("프로필사진 로드 ","프로필 사진 없음")
        }
    }
    // 갤러리에서 사진 불러오기
    override fun onClick(v: View?) {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("images/jpeg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0 && resultCode == RESULT_OK) { // 이미지 불러오기 성공했을 때, 프로필 이미지 변경
            profileImage = data!!.data!!
            binding.imageViewActivityEditProfileProfileImg.setImageURI(data!!.data!!)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    // 뒤로 가기 눌렀을 때
    override fun onBackPressed() {
        val mDialog = BottomSheetMaterialDialog.Builder(this)
            .setTitle("Stop?")
            .setAnimation("question2.json")
            .setMessage(
                "프로필 편집을 중단하실 건가요? 도중에 나가시면 저장되지 않습니다.",
                TextAlignment.CENTER
            )
            .setPositiveButton("Yes") { dialogInterface, _ ->
                super.onBackPressed()
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build();

        // Show Dialog
        mDialog.show();

    }
}