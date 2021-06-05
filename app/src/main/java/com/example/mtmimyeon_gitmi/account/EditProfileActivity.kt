package com.example.mtmimyeon_gitmi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
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
import www.sanju.motiontoast.MotionToast

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var myUserData: UserData
    private var auth = FirebaseAuth.getInstance()
    private var isImageChanged = false // 이미지가 변경됐는지 감지
    var db = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 스피너에 전공 데이터 넣기
        val majorList = resources.getStringArray(R.array.major).toMutableList()
        binding.spinnerActivityEditProfileMajor.setItem(majorList)

        // toolbar의 뒤로 가기 눌렀을 때
        binding.toolbarActivityEditProfileToolbar.setNavigationOnClickListener { onBackPressed() }

        // 이미지 클릭 시, 로컬에서 사진 불러오기
        binding.imageViewActivityEditProfileProfileImg.setOnClickListener(this)
        binding.fabActivityEditProfileCamera.setOnClickListener(this)

        // 비밀번호, 비밀번호 확인 EditText 텍스트 변경 감지 리스너
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) { // 텍스트 변경 감지
                if (binding.editTextActivityEditProfilePwValue.text.toString() == binding.editTextActivityEditProfilePwConfirmValue.text.toString()) { // 비밀번호가 일치할 때
                    binding.editTextActivityEditProfilePwConfirmValue.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(this@EditProfileActivity,
                            R.drawable.drawable_end_pw_check),
                        null)
                } else { // 비밀번호가 일치하지 않을 때
                    binding.editTextActivityEditProfilePwConfirmValue.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(this@EditProfileActivity,
                            R.drawable.drawable_end_x),
                        null)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        // 비밀번호, 비밀번호 확인 일치여부 체크 리스너 등록
        binding.editTextActivityEditProfilePwConfirmValue.addTextChangedListener(textWatcher)
        binding.editTextActivityEditProfilePwValue.addTextChangedListener(textWatcher)


        // 프로필 유저정보 초기화
        db.callUserData(auth.uid.toString(), object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                this@EditProfileActivity.myUserData = data
                var majorIndex = 0

                for (i in majorList.indices) { // major index 찾기
                    if (majorList[i] == myUserData.major) {
                        majorIndex = i
                        break
                    }
                }

                binding.editTextActivityEditProfileIdValue.setText(data.id)
                binding.editTextActivityEditProfileNameValue.setText(data.userName)
                binding.editTextActivityEditProfileStudentIdValue.setText(data.student_id)
                binding.editTextActivityEditProfilePwValue.setText(data.pw)
                binding.editTextActivityEditProfilePwConfirmValue.setText(data.pw)
                binding.editTextActivityEditProfileBirthValue.setText(data.birth)
                binding.spinnerActivityEditProfileMajor.setSelection(majorIndex)
                loadProfileImage(data.userProfileImageUrl)
            }
        })

        // 업데이트 버튼 누르면 --> 프로필 업데이트
        binding.buttonActivityEditProfileUpdateProfile.setOnClickListener {
            val updateId = binding.editTextActivityEditProfileIdValue.text.toString() //id
            val updatePw = binding.editTextActivityEditProfilePwValue.text.toString() // pw
            val updateStudentId =
                binding.editTextActivityEditProfileStudentIdValue.text.toString() // 학번
            val updateName = binding.editTextActivityEditProfileNameValue.text.toString() // 이름
            val updateBirth = binding.editTextActivityEditProfileBirthValue.text.toString() // 생일
            val updateMajor = binding.spinnerActivityEditProfileMajor.selectedItem.toString() // 전공

            if (checkProfileValidate()) { // 프로필 정보 변경 가능한지 체크(빈 문자열 없는지)
                val updatedUserData = UserData(
                    updateId,
                    updatePw,
                    updateStudentId,
                    updateName,
                    updateBirth,
                    this.myUserData.gender,
                    updateMajor,
                    this.myUserData.userProfileImageUrl
                )
                Log.d("로그",
                    "EditProfileActivity -init() called / img: ${this.myUserData.userProfileImageUrl}")

                MotionToast.createColorToast(
                    this,
                    "Information",
                    "프로필 정보를 업데이트 하는 중입니다.",
                    MotionToast.TOAST_INFO,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, R.font.maple_story_bold)
                )

                db.editUserData(this.isImageChanged,
                    updatedUserData,
                    object : Callback<Boolean> {
                        // 이미지 업로드시 callback 으로 받아오기
                        override fun onCallback(data: Boolean) {
                            if (data) { // 업데이트 성공
                                Log.d("로그", "EditProfileActivity -onCallback() called / 성공 gs://mtmi-4eeac.appspot.com/image/IMAGE_${auth.uid}.png")
                                Intent().also {
                                    it.putExtra("imgUrl", "IMAGE_${auth.uid}.png")
                                    setResult(RESULT_OK, it)
                                }
                                finish()
                            } else { // 업데이트 실패
                                Log.d("로그", "EditProfileActivity -onCallback() called / 실패")
                                MotionToast.createColorToast(
                                    this@EditProfileActivity,
                                    "Update Error",
                                    "프로필 정보를 업데이트 하는 중 알 수 없는 오류가 발생했어요.",
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(this@EditProfileActivity,
                                        R.font.maple_story_bold)
                                )
                            }
                        }
                    })
            } else { // 프로필 정보가 적절하지 않을 때
                MotionToast.createColorToast(
                    this@EditProfileActivity,
                    "Warning",
                    "필수 입력항목들을 확인해주세요.",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this@EditProfileActivity,
                        R.font.maple_story_bold)
                )
            }
        }
    }

    private fun checkProfileValidate(): Boolean { // 프로필 변경하기 전 텍스트 형식 유효한지 검사
        // 비밀번호 일치 여부
        if (binding.editTextActivityEditProfilePwValue.text.toString() != binding.editTextActivityEditProfilePwConfirmValue.text.toString())
            return false

        // 이름이 빈 문자열일 때
        if(binding.editTextActivityEditProfileNameValue.text.toString() == "")
            return false

        return true
    }

    private fun loadProfileImage(profileImageUri: String) {
        if (profileImageUri != "empty") {
            FirebaseStorage.getInstance().reference.child("image/$profileImageUri").downloadUrl.addOnSuccessListener {
                Log.d("프로필사진 로드", it.toString())
                val profileImage = binding.imageViewActivityEditProfileProfileImg
                Glide.with(applicationContext).load(it).circleCrop().into(profileImage)
            }.addOnFailureListener {
                Log.d("프로필사진 로드", "프로필사진없음")
            }
        } else {
            Log.d("프로필사진 로드 ", "프로필 사진 없음")
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

    // 갤러리로부 사진 받았을 때 오출
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) { // 이미지 불러오기 성공했을 때, 프로필 이미지 변경
            this.myUserData.userProfileImageUrl = data!!.data.toString()
            binding.imageViewActivityEditProfileProfileImg.setImageURI(data.data!!)
            this.isImageChanged = true // 이미지 변경 확인
        }
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