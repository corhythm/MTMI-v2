package com.example.mtmimyeon_gitmi.account

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityEditProfileBinding
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment

class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {

        // 스피너에 전공 데이터 넣기
        binding.spinnerActivityEditProfileMajor.setItem(
            resources.getStringArray(R.array.major).toMutableList()
        )

        // toolbar의 뒤로 가기 눌렀을 때
        binding.toolbarActivityEditProfileToolbar.setNavigationOnClickListener { onBackPressed() }

        // 이미지 클릭 시, 로컬에서 사진 불러오기
        binding.imageViewActivityEditProfileProfileImg.setOnClickListener(this)
        binding.fabActivityEditProfileCamera.setOnClickListener(this)

        // 업데이트 버튼 누르면 --> 프로필 업데이트
        binding.buttonActivityEditProfileUpdateProfile.setOnClickListener {

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