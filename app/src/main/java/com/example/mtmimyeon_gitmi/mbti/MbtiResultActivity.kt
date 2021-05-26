package com.example.mtmimyeon_gitmi.mbti

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mtmimyeon_gitmi.MapDetailsActivity
import com.example.mtmimyeon_gitmi.MapType
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMbtiResultBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.google.android.material.snackbar.Snackbar
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment

class MbtiResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMbtiResultBinding
    private val myMbtiResult = SharedPrefManager.getMyMbtiType() // 내 mbti 결과 가져오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMbtiResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val mbtiImgList = resources.obtainTypedArray(R.array.mbti_img)
        // 해당 mbti에 해당하는 values.xml index 매칭 HashMap
        val mbtiIndex = HashMap<String, Int>()
        mbtiIndex["INTJ"] = 0
        mbtiIndex["INFJ"] = 1
        mbtiIndex["ISTJ"] = 2
        mbtiIndex["ISTP"] = 3

        mbtiIndex["INTP"] = 4
        mbtiIndex["INFP"] = 5
        mbtiIndex["ISFJ"] = 6
        mbtiIndex["ISFP"] = 7

        mbtiIndex["ENTJ"] = 8
        mbtiIndex["ENFJ"] = 9
        mbtiIndex["ESTJ"] = 10
        mbtiIndex["ESTP"] = 11

        mbtiIndex["ENTP"] = 12
        mbtiIndex["ENFP"] = 13
        mbtiIndex["ESFJ"] = 14
        mbtiIndex["ESFP"] = 15


        // MBTI 이미지 설정
        binding.imageViewActivityMbtiResultMbtiResultImg.setImageResource(
            mbtiImgList.getResourceId(
                mbtiIndex[myMbtiResult]!!,
                -1
            )
        )
        // MBTI Type 설정
        binding.textViewActivityMbtiResultMbtiResultTitle.text =
            resources.getStringArray(R.array.mbti_title)[mbtiIndex[myMbtiResult]!!]
        // MBTI 성향 텍스트 설정
        binding.textViewActivityMbtiResultMbtiTypeCharacteristics.text =
            resources.getStringArray(R.array.mbti_type_characteristics)[mbtiIndex[myMbtiResult]!!]
        // MBTI 나의 팀프로젝트 성향 설정
        binding.textViewActivityMbtiResultMbtiResultMyTeamProjectType.text =
            resources.getStringArray(R.array.mbti_my_team_project_type)[mbtiIndex[myMbtiResult]!!]
        // MBTI 함께 하면 좋은 팀프로젝트 메이스 설정.
        binding.textViewActivityMbtiResultMbtiResultTeamProjectTypeWanted.text =
            resources.getStringArray(R.array.mbti_team_project_wanted)[mbtiIndex[myMbtiResult]!!]

        // 추천 장소로 이동
        binding.imageViewActivityMbtiResultRecommendedPlace.setOnClickListener {

            if (isLocationPermissionGranted()) { // 위치 정보 동의했을 때만
                val recommendedPlace =
                    resources.getStringArray(R.array.mbti_recommended_place_name)[mbtiIndex[myMbtiResult]!!]
                val latitude =
                    resources.getStringArray(R.array.mbti_recommended_places_latitude)[mbtiIndex[myMbtiResult]!!]
                val longitude =
                    resources.getStringArray(R.array.mbti_recommended_places_longitude)[mbtiIndex[myMbtiResult]!!]
                val mDialog = BottomSheetMaterialDialog.Builder(this)
                    .setTitle("추천 장소 위치 검색")
                    .setAnimation("go.json")
                    .setMessage(
                        "추천 장소인 $recommendedPlace(으)로 이동하시겠어요?",
                        TextAlignment.CENTER
                    )
                    .setPositiveButton("Yes") { dialogInterface, _ ->

                        Intent(this, MapDetailsActivity::class.java).also {
                            it.putExtra("mapType", MapType.RECOMMENDED_PLACE.typeNum)
                            it.putExtra("placeName", recommendedPlace)
                            it.putExtra("latitude", latitude)
                            it.putExtra("longitude", longitude)
                            startActivity(it)
                        }
                        overridePendingTransition(
                            R.anim.activity_slide_in,
                            R.anim.activity_slide_out
                        )

                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("No") { dialogInterface, _ -> dialogInterface.dismiss() }
                    .build();


                // Show Dialog
                mDialog.show();
            }
        }

    }

    // 위치 정보 권한 처리
    private fun isLocationPermissionGranted(): Boolean {
        val isFirstCheck = SharedPrefManager.getPermissionAccessFineLocation()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // 거부만 한 경우 사용자에게 왜 필요한지 이유를 설명해주는게 좋다
                val snackBar = Snackbar.make(
                    binding.root,
                    "학교 건물을 찾기 위해서는 위치 권한이 필요합니다",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // 처음 물었는지 여부를 저장
                    SharedPrefManager.setPermissionAccessFineLocation(false)
                    // 권한요청
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                } else {
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동한다.
                    val snackBar = Snackbar.make(
                        binding.root,
                        "위치 권한이 필요합니다. 확인를 누르면 설정 화면으로 이동합니다.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackBar.setAction("확인") {
                        Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", packageName, null)
                            startActivity(this)
                        }
                    }
                    snackBar.show()
                }
            }
            return false
        } else {
            return true
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}